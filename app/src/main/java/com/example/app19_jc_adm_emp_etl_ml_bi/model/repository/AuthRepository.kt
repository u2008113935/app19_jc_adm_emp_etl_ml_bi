package com.example.app19_jc_adm_emp_etl_ml_bi.model.repository

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.Rol
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.SessionManager
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.ApiService
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.LoginRequest
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val api: ApiService,
    private val session: SessionManager
) {
    val token: Flow<String?> = session.token
    val rol: Flow<Rol?> = session.rol
    val nombre: Flow<String?> = session.nombre

    // Login correcto → guarda token, rol y nombre en DataStore
    suspend fun login(correo: String, password: String): Result<Rol> = runCatching {
        val respuesta = api.login(LoginRequest(correo, password))
        session.guardarSesion(respuesta.token, respuesta.rol, respuesta.nombre)
        respuesta.rol
    }

    suspend fun cerrarSesion() = session.cerrarSesion()
}