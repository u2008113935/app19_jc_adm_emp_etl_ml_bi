package com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoSync

// Cola offline: todo lo que el empleado hace sin señal se guarda aquí
@Entity(tableName = "operaciones_pendientes")
data class OperacionPendienteEntity(
    @PrimaryKey val uuid: String,   // Mismo UUID que se envía a la API → no se duplica
    val tipo: String,               // CAMBIO_ESTADO_ORDEN, MERMA
    val payloadJson: String,        // Los datos de la operación en JSON
    val estado: EstadoSync = EstadoSync.PENDIENTE,
    val intentos: Int = 0,
    val error: String? = null,
    val creadoEn: Long = System.currentTimeMillis()
)