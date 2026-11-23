package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

import com.google.gson.annotations.SerializedName

// GET /api/ml/produccion-sugerida?fecha= → lee la tabla ml.produccion_sugerida
data class ProduccionSugeridaDto(
    val fecha: String,
    @SerializedName("producto_id") val productoId: Int,
    @SerializedName("producto_nombre") val productoNombre: String,
    @SerializedName("cantidad_sugerida") val cantidadSugerida: Int,
    @SerializedName("cantidad_min") val cantidadMin: Int,      // Rango del pronóstico
    @SerializedName("cantidad_max") val cantidadMax: Int,
    @SerializedName("modelo_version") val modeloVersion: String
)