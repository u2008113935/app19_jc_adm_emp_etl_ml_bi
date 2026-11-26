package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.Fechas
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.AnaliticaRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// KPIs que salen del esquema dw (ventas, pedidos, ticket, % merma, MAPE del modelo)
class TableroViewModel(private val repo: AnaliticaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TableroUiState())
    val uiState: StateFlow<TableroUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cambiarPeriodo(periodo: Periodo) {
        _uiState.update { it.copy(periodo = periodo) }
        cargar()
    }

    fun cargar() {
        val dias = _uiState.value.periodo.dias
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            // desde = hace (dias - 1) días, hasta = hoy
            repo.obtenerTablero(Fechas.desplazar(-(dias - 1)), Fechas.hoy())
                .onSuccess { t -> _uiState.update { it.copy(cargando = false, tablero = t) } }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) } }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                TableroViewModel(app.container.analiticaRepository)
            }
        }
    }
}
