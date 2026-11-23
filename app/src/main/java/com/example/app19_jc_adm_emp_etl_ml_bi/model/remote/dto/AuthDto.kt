package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.Rol

// POST /api/auth/login
data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val rol: Rol,          // "ADMIN" o "EMPLEADO" → Gson lo convierte al enum
    val nombre: String
)