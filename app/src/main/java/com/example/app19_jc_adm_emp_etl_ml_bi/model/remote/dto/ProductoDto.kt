package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductoDto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val activo: Boolean,
    @SerializedName("imagen_url") val imagenUrl: String?
)

// POST y PUT /api/productos
data class ProductoRequest(
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val activo: Boolean = true
)