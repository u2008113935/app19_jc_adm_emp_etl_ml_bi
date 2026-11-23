package com.example.app19_jc_adm_emp_etl_ml_bi.model.repository

import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.ApiService
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.ProduccionSugeridaDto

// Lee lo que el modelo de ML dejó en ml.produccion_sugerida (vía API)
class MlRepository(private val api: ApiService) {

    suspend fun obtenerProduccionSugerida(fecha: String): Result<List<ProduccionSugeridaDto>> =
        runCatching { api.obtenerProduccionSugerida(fecha) }
}