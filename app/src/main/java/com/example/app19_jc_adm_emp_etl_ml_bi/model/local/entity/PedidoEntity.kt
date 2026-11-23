package com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey val id: String,     // UUID generado por quien crea el pedido (bot o app)
    val codigo: String,             // Ej. DEL-000123, el que ve el cliente
    val clienteNombre: String,
    val clienteTelefono: String,
    val canal: String,              // WHATSAPP o MOSTRADOR
    val total: Double,
    val fechaEntrega: String,       // "2026-11-05"
    val horaEntrega: String,        // "15:30"
    val estado: EstadoPedido,
    val actualizadoEn: Long = System.currentTimeMillis()
)