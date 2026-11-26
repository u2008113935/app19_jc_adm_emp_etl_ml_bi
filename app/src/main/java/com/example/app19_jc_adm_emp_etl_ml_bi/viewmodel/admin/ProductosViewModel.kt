package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.ProductoEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.ProductoRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductosUiState(
    val productos: List<ProductoEntity> = emptyList(),
    val refrescando: Boolean = false,
    val error: String? = null
)

// Lista de productos del ADMIN (incluye inactivos). El formulario tiene su propio ViewModel.
class ProductosViewModel(private val repo: ProductoRepository) : ViewModel() {

    private val refrescando = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProductosUiState> =
        combine(repo.observarProductos(), refrescando, error) { lista, r, e ->
            ProductosUiState(lista, r, e)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProductosUiState())

    init { refrescar() }

    fun refrescar() {
        viewModelScope.launch {
            refrescando.value = true
            error.value = null
            repo.refrescar().onFailure { error.value = mensajeDeError(it) }
            refrescando.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                ProductosViewModel(app.container.productoRepository)
            }
        }
    }
}
