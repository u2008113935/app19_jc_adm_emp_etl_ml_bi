package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.AuthRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) = _uiState.update { it.copy(correo = valor.trim(), error = null) }
    fun onPasswordChange(valor: String) = _uiState.update { it.copy(password = valor, error = null) }

    fun alternarMostrarPassword() =
        _uiState.update { it.copy(mostrarPassword = !it.mostrarPassword) }

    fun ingresar() {
        val estado = _uiState.value
        if (estado.correo.isBlank() || estado.password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }

            authRepository.login(estado.correo, estado.password)
                .onSuccess { rol ->
                    _uiState.update { it.copy(cargando = false, rolIngresado = rol) }
                }
                .onFailure { e ->
                    // La API manda el motivo: "Correo o contraseña incorrectos", "Usuario desactivado"...
                    _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
                }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                LoginViewModel(app.container.authRepository)
            }
        }
    }
}
