package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.Fechas
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.PlanItem
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.ProduccionSugeridaDto
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.MlRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.ProduccionRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Una fila del plan: lo que sugirió el modelo + lo que decide el admin
data class FilaPlan(
    val sugerencia: ProduccionSugeridaDto,
    val cantidad: String                      // editable; empieza con la sugerida
)

data class PlanUiState(
    val fecha: String = Fechas.manana(),
    val cargando: Boolean = true,
    val filas: List<FilaPlan> = emptyList(),
    val aprobando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)

// El ML propone, el ADMIN decide: puede ajustar cada cantidad antes de aprobar
class PlanProduccionViewModel(
    private val mlRepo: MlRepository,
    private val produccionRepo: ProduccionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cambiarFecha(fecha: String) {
        _uiState.update { it.copy(fecha = fecha, mensaje = null) }
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            mlRepo.obtenerProduccionSugerida(_uiState.value.fecha)
                .onSuccess { lista ->
                    _uiState.update { s ->
                        s.copy(cargando = false, filas = lista.map { FilaPlan(it, it.cantidadSugerida.toString()) })
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) } }
        }
    }

    fun cambiarCantidad(productoId: Int, valor: String) {
        if (valor.isNotEmpty() && valor.any { !it.isDigit() }) return
        _uiState.update { s ->
            s.copy(filas = s.filas.map { if (it.sugerencia.productoId == productoId) it.copy(cantidad = valor) else it })
        }
    }

    // Crea las órdenes de producción para esa fecha → aparecen en la cola del empleado
    fun aprobar() {
        val s = _uiState.value
        val items = s.filas.mapNotNull { f ->
            f.cantidad.toIntOrNull()?.takeIf { it > 0 }?.let { PlanItem(f.sugerencia.productoId, it) }
        }
        if (items.isEmpty()) {
            _uiState.update { it.copy(error = "Ingresa al menos una cantidad") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(aprobando = true, error = null, mensaje = null) }
            produccionRepo.aprobarPlan(s.fecha, items)
                .onSuccess {
                    _uiState.update { it.copy(aprobando = false, mensaje = "Plan aprobado: ${items.size} órdenes para el ${s.fecha}") }
                }
                .onFailure { e -> _uiState.update { it.copy(aprobando = false, error = mensajeDeError(e)) } }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                PlanProduccionViewModel(app.container.mlRepository, app.container.produccionRepository)
            }
        }
    }
}
