package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto

// GET /api/kpis?desde=&hasta= → lee las vistas del esquema dw
data class TableroDto(
    val desde: String,
    val hasta: String,
    val kpis: List<KpiDto>,
    val ventasPorDia: List<SerieDto>,
    val mermaPorProducto: List<SerieDto>
)

data class KpiDto(
    val nombre: String,        // "Ventas", "% Merma", "MAPE"...
    val valor: Double,
    val unidad: String,        // "S/", "%", "pedidos"
    val variacion: Double?     // vs. periodo anterior; null si no aplica
)

data class SerieDto(
    val etiqueta: String,      // "Lun", "Torta de chocolate"...
    val valor: Double
)