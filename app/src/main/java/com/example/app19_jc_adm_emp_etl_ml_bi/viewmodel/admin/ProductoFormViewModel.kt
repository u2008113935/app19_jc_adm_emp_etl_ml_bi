package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.ProductoRequest
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.ProductoRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val CATEGORIAS = listOf("TORTAS", "POSTRES", "PANES", "BOCADITOS")

data class ProductoFormUiState(
    val esNuevo: Boolean = true,
    val nombre: String = "",
    val categoria: String = "TORTAS",
    val precio: String = "",                 // texto: así el usuario puede escribir "12."
    val activo: Boolean = true,
    val guardando: Boolean = false,
    val error: String? = null,
    val guardado: Boolean = false            // true → volver a la lista
)

class ProductoFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ProductoRepository
) : ViewModel() {

    // -1 = producto nuevo
    private val productoId: Int? = savedStateHandle.get<Int>("id")?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(ProductoFormUiState(esNuevo = productoId == null))
    val uiState: StateFlow<ProductoFormUiState> = _uiState.asStateFlow()

    init {
        // Si es edición, se llena con lo que hay en Room
        productoId?.let { id ->
            viewModelScope.launch {
                repo.buscarPorId(id)?.let { p ->
                    _uiState.update {
                        it.copy(nombre = p.nombre, categoria = p.categoria, precio = p.precio.toString(), activo = p.activo)
                    }
                }
            }
        }
    }

    fun onNombre(valor: String) = _uiState.update { it.copy(nombre = valor, error = null) }
    fun onCategoria(valor: String) = _uiState.update { it.copy(categoria = valor) }
    fun onPrecio(valor: String) = _uiState.update { it.copy(precio = valor.replace(',', '.'), error = null) }
    fun onActivo(valor: Boolean) = _uiState.update { it.copy(activo = valor) }

    fun guardar() {
        val s = _uiState.value
        val precio = s.precio.toDoubleOrNull()
        when {
            s.nombre.isBlank() -> { _uiState.update { it.copy(error = "Escribe el nombre") }; return }
            precio == null || precio <= 0 -> { _uiState.update { it.copy(error = "Precio inválido") }; return }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, error = null) }
            repo.guardar(productoId, ProductoRequest(s.nombre.trim(), s.categoria, precio!!, s.activo))
                .onSuccess { _uiState.update { it.copy(guardando = false, guardado = true) } }
                .onFailure { e -> _uiState.update { it.copy(guardando = false, error = mensajeDeError(e)) } }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                ProductoFormViewModel(createSavedStateHandle(), app.container.productoRepository)
            }
        }
    }
}
