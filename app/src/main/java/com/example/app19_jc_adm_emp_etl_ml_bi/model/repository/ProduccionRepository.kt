package com.example.app19_jc_adm_emp_etl_ml_bi.model.repository

import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.OperacionPendienteDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.OrdenProduccionDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OperacionPendienteEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OrdenProduccionEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.ApiService
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.CambioEstadoOrdenRequest
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.OrdenDto
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.PlanItem
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.PlanRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProduccionRepository(
    private val api: ApiService,
    private val ordenDao: OrdenProduccionDao,
    private val operacionDao: OperacionPendienteDao,
    private val gson: Gson,
    private val programarSync: () -> Unit      // Lo provee el AppContainer (WorkManager)
) {
    fun observarCola(fecha: String): Flow<List<OrdenProduccionEntity>> = ordenDao.observarCola(fecha)

    fun observarPendientes(): Flow<Int> = operacionDao.observarCantidadPendientes()

    suspend fun buscarPorId(id: String): OrdenProduccionEntity? = ordenDao.buscarPorId(id)

    suspend fun refrescar(fecha: String): Result<Unit> = runCatching {
        ordenDao.guardarTodas(api.obtenerOrdenes(fecha).map { it.toEntity() })
    }

    // OFFLINE-FIRST: 1) cambia en Room  2) deja la operación en cola  3) pide sincronizar
    suspend fun cambiarEstado(ordenId: String, estado: EstadoOrden) {
        ordenDao.actualizarEstado(ordenId, estado)

        val uuid = UUID.randomUUID().toString()
        val request = CambioEstadoOrdenRequest(uuid = uuid, estado = estado)
        operacionDao.insertar(
            OperacionPendienteEntity(
                uuid = uuid,
                tipo = TIPO_CAMBIO_ESTADO_ORDEN,
                payloadJson = gson.toJson(mapOf("ordenId" to ordenId, "request" to request))
            )
        )
        programarSync()
    }

    // Aprobar el plan del ML requiere señal (crea órdenes en el servidor)
    suspend fun aprobarPlan(fecha: String, items: List<PlanItem>): Result<Unit> = runCatching {
        val ordenes = api.aprobarPlan(PlanRequest(fecha, items))
        ordenDao.guardarTodas(ordenes.map { it.toEntity() })
    }

    private fun OrdenDto.toEntity() = OrdenProduccionEntity(
        id = id,
        pedidoId = pedidoId,
        productoId = productoId,
        productoNombre = productoNombre,
        cantidad = cantidad,
        fechaProduccion = fechaProduccion,
        horaEntrega = horaEntrega,
        estado = estado
    )

    companion object {
        const val TIPO_CAMBIO_ESTADO_ORDEN = "CAMBIO_ESTADO_ORDEN"
    }
}