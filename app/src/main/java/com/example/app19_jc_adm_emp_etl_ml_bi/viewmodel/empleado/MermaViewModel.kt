package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.empleado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.Fechas
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.ProductoEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.MermaRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.ProduccionRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Motivos válidos (los mismos que acepta la API)
val MOTIVOS_MERMA = listOf("NO_VENDIDO", "DAÑADO", "VENCIDO")

data class MermaForm(
    val productoId: Int? = null,
    val cantidad: String = "",
    val motivo: String = "NO_VENDIDO",
    val error: String? = null,
    val mensaje: String? = null
)

data class MermaUiState(
    val productos: List<ProductoEntity> = emptyList(),
    val form: MermaForm = MermaForm(),
    val pendientesSync: Int = 0
)

// Cierre del día: lo que no se vendió o se dañó. Se guarda aunque no haya señal.
class MermaViewModel(
    productoRepo: ProductoRepository,
    private val mermaRepo: MermaRepository,
    produccionRepo: ProduccionRepository
) : ViewModel() {

    private val form = MutableStateFlow(MermaForm())

    val uiState: StateFlow<MermaUiState> = combine(
        productoRepo.observarProductos().map { lista -> lista.filter { it.activo } },
        form,
        produccionRepo.observarPendientes()
    ) { productos, f, pend -> MermaUiState(productos, f, pend) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MermaUiState())

    init {
        // Si hay señal, actualiza el catálogo guardado en Room
        viewModelScope.launch { productoRepo.refrescar() }
    }

    fun elegirProducto(id: Int) = form.update { it.copy(productoId = id, error = null, mensaje = null) }
    fun onCantidad(valor: String) {
        if (valor.all { it.isDigit() }) form.update { it.copy(cantidad = valor, error = null, mensaje = null) }
    }
    fun elegirMotivo(m: String) = form.update { it.copy(motivo = m) }

    fun registrar() {
        val f = form.value
        val cantidad = f.cantidad.toIntOrNull()
        when {
            f.productoId == null -> { form.update { it.copy(error = "Elige un producto") }; return }
            cantidad == null || cantidad <= 0 -> { form.update { it.copy(error = "Cantidad inválida") }; return }
        }
        viewModelScope.launch {
            mermaRepo.registrar(Fechas.hoy(), f.productoId!!, cantidad!!, f.motivo)
            form.value = MermaForm(mensaje = "Merma guardada. Se enviará a la API al tener señal.")
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                MermaViewModel(
                    app.container.productoRepository,
                    app.container.mermaRepository,
                    app.container.produccionRepository
                )
            }
        }
    }
}
