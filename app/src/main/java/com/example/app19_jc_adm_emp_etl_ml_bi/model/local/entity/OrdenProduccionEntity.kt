package com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden

@Entity(tableName = "ordenes_produccion")
data class OrdenProduccionEntity(
    @PrimaryKey val id: String,     // UUID
    val pedidoId: String?,          // null si viene del plan de producción (ML) y no de un pedido
    val productoId: Int,
    val productoNombre: String,
    val cantidad: Int,
    val fechaProduccion: String,    // "2026-11-05"
    val horaEntrega: String,        // Para ordenar la cola del empleado
    val estado: EstadoOrden
)