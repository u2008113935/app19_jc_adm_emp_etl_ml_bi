package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.empleado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.Fechas
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OrdenProduccionEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.ProduccionRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ColaUiState(
    val fecha: String = Fechas.hoy(),
    val ordenes: List<OrdenProduccionEntity> = emptyList(),
    val pendientesSync: Int = 0,             // cambios que aún no llegan a la API
    val refrescando: Boolean = false,
    val aviso: String? = null                // sin señal: se trabaja con lo guardado
) {
    val porHacer get() = ordenes.count { it.estado != EstadoOrden.TERMINADA }
}

// Cola del EMPLEADO: OFFLINE-FIRST. Lee de Room; los cambios se guardan
// primero en el celular y el SyncWorker los envía cuando haya señal.
class ColaViewModel(private val repo: ProduccionRepository) : ViewModel() {

    private val fecha = Fechas.hoy()
    private val refrescando = MutableStateFlow(false)
    private val aviso = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ColaUiState> =
        combine(repo.observarCola(fecha), repo.observarPendientes(), refrescando, aviso) { ordenes, pend, r, a ->
            ColaUiState(fecha, ordenes, pend, r, a)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ColaUiState())

    init { refrescar() }

    fun refrescar() {
        viewModelScope.launch {
            refrescando.value = true
            aviso.value = null
            repo.refrescar(fecha).onFailure { e ->
                aviso.value = "${mensajeDeError(e)}. Mostrando lo guardado en el celular."
            }
            refrescando.value = false
        }
    }

    // Funciona sin señal: cambia en Room y deja la operación en cola
    fun avanzar(orden: OrdenProduccionEntity) {
        val siguiente = when (orden.estado) {
            EstadoOrden.PLANIFICADA -> EstadoOrden.EN_PROCESO
            EstadoOrden.EN_PROCESO -> EstadoOrden.TERMINADA
            EstadoOrden.TERMINADA -> return
        }
        viewModelScope.launch { repo.cambiarEstado(orden.id, siguiente) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                ColaViewModel(app.container.produccionRepository)
            }
        }
    }
}
