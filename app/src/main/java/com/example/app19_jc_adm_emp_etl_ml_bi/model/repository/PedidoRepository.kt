package com.example.app19_jc_adm_emp_etl_ml_bi.model.repository

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.PedidoDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.PedidoEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.ApiService
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.CambioEstadoPedidoRequest
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.PedidoDetalleDto
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.PedidoDto
import kotlinx.coroutines.flow.Flow

class PedidoRepository(
    private val api: ApiService,
    private val dao: PedidoDao
) {
    // La pantalla SIEMPRE lee de Room (funciona aunque no haya señal)
    fun observarPedidos(estado: EstadoPedido? = null): Flow<List<PedidoEntity>> =
        if (estado == null) dao.observarTodos() else dao.observarPorEstado(estado)

    suspend fun buscarPorId(id: String): PedidoEntity? = dao.buscarPorId(id)

    // Trae los pedidos de la API y actualiza Room → el Flow avisa a la pantalla
    suspend fun refrescar(): Result<Unit> = runCatching {
        val pedidos = api.obtenerPedidos()
        dao.guardarTodos(pedidos.map { it.toEntity() })
    }

    // El detalle (productos, historial, pagos) se pide en línea a la API
    suspend fun obtenerDetalle(id: String): Result<PedidoDetalleDto> = runCatching {
        api.obtenerPedido(id)
    }

    // Cambiar estado requiere señal: la API valida si la transición es permitida
    suspend fun cambiarEstado(id: String, estado: EstadoPedido): Result<Unit> = runCatching {
        val actualizado = api.cambiarEstadoPedido(id, CambioEstadoPedidoRequest(estado))
        dao.guardarTodos(listOf(actualizado.toEntity()))
    }

    // DTO (forma de la API) → Entity (forma de Room)
    private fun PedidoDto.toEntity() = PedidoEntity(
        id = id,
        codigo = codigo,
        clienteNombre = clienteNombre,
        clienteTelefono = clienteTelefono,
        canal = canal,
        total = total,
        fechaEntrega = fechaEntrega,
        horaEntrega = horaEntrega,
        estado = estado
    )
}
