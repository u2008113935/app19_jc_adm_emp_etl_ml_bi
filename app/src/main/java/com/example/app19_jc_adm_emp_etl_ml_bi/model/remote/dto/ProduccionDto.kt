package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.google.gson.annotations.SerializedName

// GET /api/produccion/ordenes?fecha=
data class OrdenDto(
    val id: String,
    @SerializedName("pedido_id") val pedidoId: String?,
    @SerializedName("producto_id") val productoId: Int,
    @SerializedName("producto_nombre") val productoNombre: String,
    val cantidad: Int,
    @SerializedName("fecha_produccion") val fechaProduccion: String,
    @SerializedName("hora_entrega") val horaEntrega: String,
    val estado: EstadoOrden
)

// PATCH /api/produccion/ordenes/{id}/estado (lo envía el SyncWorker)
data class CambioEstadoOrdenRequest(
    val uuid: String,                                    // Evita duplicados si se reenvía
    val estado: EstadoOrden
)

// POST /api/produccion/plan → el admin aprueba lo que sugirió el ML
data class PlanRequest(
    val fecha: String,
    val items: List<PlanItem>
)

data class PlanItem(
    @SerializedName("producto_id") val productoId: Int,
    val cantidad: Int
)