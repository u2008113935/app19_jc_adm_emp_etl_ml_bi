package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.empleado

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OrdenProduccionEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.ProduccionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Detalle de una orden (lee de Room: funciona sin señal)
class OrdenDetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ProduccionRepository
) : ViewModel() {

    private val ordenId: String = checkNotNull(savedStateHandle["id"])
    private val _orden = MutableStateFlow<OrdenProduccionEntity?>(null)
    val orden: StateFlow<OrdenProduccionEntity?> = _orden.asStateFlow()

    init { cargar() }

    private fun cargar() {
        viewModelScope.launch { _orden.value = repo.buscarPorId(ordenId) }
    }

    fun cambiarEstado(nuevo: EstadoOrden) {
        viewModelScope.launch {
            repo.cambiarEstado(ordenId, nuevo)     // Room + cola offline
            cargar()
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                OrdenDetalleViewModel(createSavedStateHandle(), app.container.produccionRepository)
            }
        }
    }
}
