package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

import com.google.gson.annotations.SerializedName

// POST /api/merma → cierre del día; luego el ETL la lleva al dw
data class MermaRequest(
    val uuid: String,
    val fecha: String,
    @SerializedName("producto_id") val productoId: Int,
    val cantidad: Int,
    val motivo: String                                   // NO_VENDIDO, DAÑADO, VENCIDO
)