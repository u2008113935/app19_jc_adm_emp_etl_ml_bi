package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.google.gson.annotations.SerializedName

// GET /api/pedidos  → una fila de la bandeja
data class PedidoDto(
    val id: String,
    val codigo: String,
    @SerializedName("cliente_nombre") val clienteNombre: String,
    @SerializedName("cliente_telefono") val clienteTelefono: String,
    val canal: String,                                   // WHATSAPP, MOSTRADOR, WEB o APP
    val total: Double,
    @SerializedName("fecha_entrega") val fechaEntrega: String,
    @SerializedName("hora_entrega") val horaEntrega: String,
    val estado: EstadoPedido
)

// PATCH /api/pedidos/{id}/estado → la API valida si la transición es permitida
data class CambioEstadoPedidoRequest(
    val estado: EstadoPedido
)

// GET /api/pedidos/{id} → cabecera + productos + historial + órdenes + pagos
data class PedidoDetalleDto(
    val id: String,
    val codigo: String,
    @SerializedName("cliente_nombre") val clienteNombre: String,
    @SerializedName("cliente_telefono") val clienteTelefono: String,
    val canal: String,
    val total: Double,
    @SerializedName("fecha_entrega") val fechaEntrega: String,
    @SerializedName("hora_entrega") val horaEntrega: String,
    val estado: EstadoPedido,
    val nota: String?,
    val items: List<ItemPedidoDto>,
    val historial: List<HistorialDto>,
    val ordenes: List<OrdenResumenDto>,
    val pagos: List<PagoResumenDto>
)

data class ItemPedidoDto(
    @SerializedName("producto_nombre") val productoNombre: String,
    val cantidad: Int,
    @SerializedName("precio_unit") val precioUnit: Double,
    val subtotal: Double
)

data class HistorialDto(
    @SerializedName("estado_nuevo") val estadoNuevo: String,
    val fecha: String,                                   // "2026-11-23 15:30"
    val usuario: String?,
    val rol: String?,
    val nota: String?
)

data class OrdenResumenDto(
    @SerializedName("producto_nombre") val productoNombre: String,
    val cantidad: Int,
    val estado: String
)

data class PagoResumenDto(
    @SerializedName("pago_id") val pagoId: String,
    val estado: String,
    @SerializedName("nro_operacion_banco") val nroOperacionBanco: String?
)
