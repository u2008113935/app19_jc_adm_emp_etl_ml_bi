package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.TableroDto

// Periodos que se pueden elegir en el tablero
enum class Periodo(val texto: String, val dias: Int) {
    SEMANA("7 días", 7),
    MES("30 días", 30)
}

data class TableroUiState(
    val periodo: Periodo = Periodo.SEMANA,
    val cargando: Boolean = true,
    val tablero: TableroDto? = null,
    val error: String? = null
)
