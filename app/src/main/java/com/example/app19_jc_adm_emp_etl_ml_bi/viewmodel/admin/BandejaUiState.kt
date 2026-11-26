package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.PedidoEntity

data class BandejaUiState(
    val filtro: EstadoPedido? = null,           // null = Todos
    val pedidos: List<PedidoEntity> = emptyList(),
    val refrescando: Boolean = false,
    val error: String? = null
)
