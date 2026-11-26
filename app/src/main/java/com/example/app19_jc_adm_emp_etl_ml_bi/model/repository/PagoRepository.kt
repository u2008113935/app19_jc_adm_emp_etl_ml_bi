package com.example.app19_jc_adm_emp_etl_ml_bi.model.repository

import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.ApiService
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.PagoEstadoDto
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.QrRequest
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.QrResponse

// Banco Andino: SOLO en línea. Un pago nunca se da por confirmado sin el banco.
class PagoRepository(private val api: ApiService) {

    suspend fun generarQr(pedidoId: String): Result<QrResponse> =
        runCatching { api.generarQr(QrRequest(pedidoId)) }

    suspend fun consultarPago(pagoId: String): Result<PagoEstadoDto> =
        runCatching { api.consultarPago(pagoId) }

    // Demo en clase: la API hace de banco y confirma
    suspend fun simularConfirmacion(pagoId: String): Result<PagoEstadoDto> =
        runCatching { api.simularPago(pagoId, mapOf("resultado" to "CONFIRMADO")) }
}
