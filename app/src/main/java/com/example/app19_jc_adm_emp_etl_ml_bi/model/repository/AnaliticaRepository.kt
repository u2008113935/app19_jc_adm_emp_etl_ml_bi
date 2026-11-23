package com.example.app19_jc_adm_emp_etl_ml_bi.model.repository

import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.ApiService
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.TableroDto

// KPIs calculados desde el esquema dw (después del ETL)
class AnaliticaRepository(private val api: ApiService) {

    suspend fun obtenerTablero(desde: String, hasta: String): Result<TableroDto> =
        runCatching { api.obtenerTablero(desde, hasta) }
}