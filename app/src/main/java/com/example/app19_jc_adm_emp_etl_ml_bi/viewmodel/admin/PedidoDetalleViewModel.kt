package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.PedidoDetalleDto
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.PedidoRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PedidoDetalleUiState(
    val cargando: Boolean = true,
    val pedido: PedidoDetalleDto? = null,
    val procesando: Boolean = false,              // mientras se cambia el estado
    val error: String? = null,
    val mensaje: String? = null
)

// Acciones que el ADMIN puede hacer según el estado (mismas reglas que la API)
enum class AccionPedido(val texto: String, val destino: EstadoPedido) {
    COBRAR_EFECTIVO("Pagado en efectivo", EstadoPedido.PAGADO),
    ENTREGAR("Marcar entregado", EstadoPedido.ENTREGADO),
    CANCELAR("Cancelar pedido", EstadoPedido.CANCELADO)
}

class PedidoDetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: PedidoRepository
) : ViewModel() {

    private val pedidoId: String = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(PedidoDetalleUiState())
    val uiState: StateFlow<PedidoDetalleUiState> = _uiState.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            repo.obtenerDetalle(pedidoId)
                .onSuccess { p -> _uiState.update { it.copy(cargando = false, pedido = p) } }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) } }
        }
    }

    // Qué botones mostrar
    fun accionesDisponibles(estado: EstadoPedido): List<AccionPedido> = when (estado) {
        EstadoPedido.PEND_PAGO -> listOf(AccionPedido.COBRAR_EFECTIVO, AccionPedido.CANCELAR)
        EstadoPedido.PAGADO -> listOf(AccionPedido.CANCELAR)
        EstadoPedido.LISTO -> listOf(AccionPedido.ENTREGAR)
        else -> emptyList()
    }

    fun ejecutar(accion: AccionPedido) {
        viewModelScope.launch {
            _uiState.update { it.copy(procesando = true, error = null, mensaje = null) }
            repo.cambiarEstado(pedidoId, accion.destino)
                .onSuccess {
                    _uiState.update { it.copy(procesando = false, mensaje = "Listo: ${accion.texto.lowercase()}") }
                    cargar()
                }
                .onFailure { e -> _uiState.update { it.copy(procesando = false, error = mensajeDeError(e)) } }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                PedidoDetalleViewModel(createSavedStateHandle(), app.container.pedidoRepository)
            }
        }
    }
}
