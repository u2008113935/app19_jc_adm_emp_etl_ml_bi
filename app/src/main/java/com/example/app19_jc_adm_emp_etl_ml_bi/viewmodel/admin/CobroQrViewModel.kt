package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.QrResponse
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.PagoRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.PedidoRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class CobroQrUiState(
    val cargando: Boolean = true,
    val qr: QrResponse? = null,
    val estadoPago: String = "PENDIENTE",          // PENDIENTE, CONFIRMADO, RECHAZADO
    val nroOperacion: String? = null,
    val error: String? = null
)

class CobroQrViewModel(
    savedStateHandle: SavedStateHandle,
    private val pagoRepo: PagoRepository,
    private val pedidoRepo: PedidoRepository
) : ViewModel() {

    private val pedidoId: String = checkNotNull(savedStateHandle["pedidoId"])
    private val _uiState = MutableStateFlow(CobroQrUiState())
    val uiState: StateFlow<CobroQrUiState> = _uiState.asStateFlow()
    private var consulta: Job? = null

    init { generar() }

    // 1) La API pide el QR a Banco Andino (si ya existe uno pendiente, lo reutiliza)
    fun generar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            pagoRepo.generarQr(pedidoId)
                .onSuccess { qr ->
                    _uiState.update { it.copy(cargando = false, qr = qr, estadoPago = qr.estado) }
                    esperarConfirmacion(qr.pagoId)
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) } }
        }
    }

    // 2) Pregunta cada 3 segundos hasta que el banco confirme o rechace
    private fun esperarConfirmacion(pagoId: String) {
        consulta?.cancel()
        consulta = viewModelScope.launch {
            while (isActive) {
                delay(3_000)
                pagoRepo.consultarPago(pagoId).onSuccess { pago ->
                    _uiState.update { it.copy(estadoPago = pago.estado, nroOperacion = pago.nroOperacionBanco) }
                    if (pago.estado != "PENDIENTE") {
                        pedidoRepo.refrescar()            // la bandeja ya muestra PAGADO
                        return@launch
                    }
                }
            }
        }
    }

    // Solo demo en clase: hace de banco y confirma el pago
    fun simularPagoBanco() {
        val pagoId = _uiState.value.qr?.pagoId ?: return
        viewModelScope.launch {
            pagoRepo.simularConfirmacion(pagoId)
                .onFailure { e -> _uiState.update { it.copy(error = mensajeDeError(e)) } }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                CobroQrViewModel(createSavedStateHandle(), app.container.pagoRepository, app.container.pedidoRepository)
            }
        }
    }
}
