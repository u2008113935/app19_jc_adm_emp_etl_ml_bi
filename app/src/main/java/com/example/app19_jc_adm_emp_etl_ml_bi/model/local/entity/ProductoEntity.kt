package com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: String,          // TORTAS, POSTRES, PANES, BOCADITOS
    val precio: Double,
    val activo: Boolean = true,
    val imagenUrl: String? = null
)