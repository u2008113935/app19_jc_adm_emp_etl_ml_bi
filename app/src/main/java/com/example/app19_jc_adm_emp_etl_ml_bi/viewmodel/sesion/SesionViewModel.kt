package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.sesion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Nombre del usuario para la barra superior y botón "Salir"
class SesionViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val nombre: StateFlow<String?> =
        authRepository.nombre.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun cerrarSesion(alTerminar: () -> Unit) {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            alTerminar()
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                SesionViewModel(app.container.authRepository)
            }
        }
    }
}
