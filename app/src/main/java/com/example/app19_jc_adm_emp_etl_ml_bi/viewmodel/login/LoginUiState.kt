package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.login

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.Rol

data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val mostrarPassword: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val rolIngresado: Rol? = null   // != null → login correcto, navegar
)