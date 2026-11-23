package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.Rol
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

// A dónde debe ir la app después del Splash
sealed interface SplashDestino {
    data object Cargando : SplashDestino
    data object Login : SplashDestino
    data object Admin : SplashDestino
    data object Empleado : SplashDestino
}

class SplashViewModel(authRepository: AuthRepository) : ViewModel() {

    val destino: StateFlow<SplashDestino> = flow {
        delay(1200) // deja ver el logo un momento
        emitAll(
            combine(authRepository.token, authRepository.rol) { token, rol ->
                when {
                    token.isNullOrBlank() || rol == null -> SplashDestino.Login
                    rol == Rol.ADMIN -> SplashDestino.Admin
                    else -> SplashDestino.Empleado
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SplashDestino.Cargando)

    // Cómo se crea este ViewModel: pide el repositorio al AppContainer
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App19Application
                SplashViewModel(app.container.authRepository)
            }
        }
    }
}