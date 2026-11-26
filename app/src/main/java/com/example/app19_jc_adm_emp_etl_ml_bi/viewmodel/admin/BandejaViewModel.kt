package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.PedidoRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BandejaViewModel(private val repo: PedidoRepository) : ViewModel() {

    private val filtro = MutableStateFlow<EstadoPedido?>(null)
    private val carga = MutableStateFlow(Pair(false, null as String?))   // (refrescando, error)

    // Cada vez que cambia el filtro, se observa otra consulta de Room
    private val pedidos = filtro.flatMapLatest { repo.observarPedidos(it) }

    val uiState: StateFlow<BandejaUiState> =
        combine(filtro, pedidos, carga) { f, lista, (refrescando, error) ->
            BandejaUiState(filtro = f, pedidos = lista, refrescando = refrescando, error = error)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandejaUiState())

    init { refrescar() }

    fun seleccionarFiltro(estado: EstadoPedido?) { filtro.value = estado }

    // Trae de la API y guarda en Room; si no hay señal, se ve lo último guardado
    fun refrescar() {
        viewModelScope.launch {
            carga.value = true to null
            repo.refrescar()
                .onSuccess { carga.value = false to null }
                .onFailure { e -> carga.update { false to mensajeDeError(e) } }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                BandejaViewModel(app.container.pedidoRepository)
            }
        }
    }
}
