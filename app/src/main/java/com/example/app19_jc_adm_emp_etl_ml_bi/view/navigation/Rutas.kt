package com.example.app19_jc_adm_emp_etl_ml_bi.view.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"

    // ── Admin ──
    const val BANDEJA = "admin/bandeja"
    const val PEDIDO_DETALLE = "admin/pedido/{id}"
    const val COBRO_QR = "admin/cobro/{pedidoId}"
    const val PRODUCTOS = "admin/productos"
    const val PRODUCTO_FORM = "admin/producto/{id}"          // id = -1 → nuevo
    const val PLAN = "admin/plan"
    const val TABLERO = "admin/tablero"

    // ── Empleado ──
    const val COLA = "empleado/cola"
    const val ORDEN_DETALLE = "empleado/orden/{id}"
    const val MERMA = "empleado/merma"

    // Rutas con datos
    fun pedidoDetalle(id: String) = "admin/pedido/$id"
    fun cobroQr(pedidoId: String) = "admin/cobro/$pedidoId"
    fun productoForm(id: Int?) = "admin/producto/${id ?: -1}"
    fun ordenDetalle(id: String) = "empleado/orden/$id"
}
