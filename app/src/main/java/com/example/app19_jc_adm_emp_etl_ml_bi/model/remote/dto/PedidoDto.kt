package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.google.gson.annotations.SerializedName

data class PedidoDto(
    val id: String,
    val codigo: String,
    @SerializedName("cliente_nombre") val clienteNombre: String,
    @SerializedName("cliente_telefono") val clienteTelefono: String,
    val canal: String,                                   // WHATSAPP o MOSTRADOR
    val total: Double,
    @SerializedName("fecha_entrega") val fechaEntrega: String,
    @SerializedName("hora_entrega") val horaEntrega: String,
    val estado: EstadoPedido
)

// PATCH /api/pedidos/{id}/estado → la API valida si la transición es permitida
data class CambioEstadoPedidoRequest(
    val estado: EstadoPedido
)