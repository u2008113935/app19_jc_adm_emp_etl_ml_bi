package com.example.app19_jc_adm_emp_etl_ml_bi.model.repository

import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.OperacionPendienteDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OperacionPendienteEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.MermaRequest
import com.google.gson.Gson
import java.util.UUID

// La merma se registra al cierre del día, aunque no haya señal.
// Llega a la API → BD operacional → el ETL nocturno la lleva al dw → mejora el ML.
class MermaRepository(
    private val operacionDao: OperacionPendienteDao,
    private val gson: Gson,
    private val programarSync: () -> Unit
) {
    suspend fun registrar(fecha: String, productoId: Int, cantidad: Int, motivo: String) {
        val uuid = UUID.randomUUID().toString()
        val request = MermaRequest(uuid, fecha, productoId, cantidad, motivo)

        operacionDao.insertar(
            OperacionPendienteEntity(
                uuid = uuid,
                tipo = TIPO_MERMA,
                payloadJson = gson.toJson(request)
            )
        )
        programarSync()
    }

    companion object {
        const val TIPO_MERMA = "MERMA"
    }
}