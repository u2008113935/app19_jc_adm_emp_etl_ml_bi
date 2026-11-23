package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

import com.google.gson.annotations.SerializedName

// POST /api/pagos/qr → la API le pide el cobro a Banco Andino
data class QrRequest(
    @SerializedName("pedido_id") val pedidoId: String
)

data class QrResponse(
    @SerializedName("pago_id") val pagoId: String,
    val referencia: String,                              // Ej. DEL-000123
    @SerializedName("qr_texto") val qrTexto: String,     // Contenido del QR que genera el banco
    val monto: Double,
    val estado: String                                   // PENDIENTE
)

// GET /api/pagos/{id}
data class PagoEstadoDto(
    @SerializedName("pago_id") val pagoId: String,
    val estado: String,                                  // PENDIENTE, CONFIRMADO, RECHAZADO
    @SerializedName("nro_operacion_banco") val nroOperacionBanco: String?
)