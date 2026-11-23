package com.example.app19_jc_adm_emp_etl_ml_bi.model

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.app19_jc_adm_emp_etl_ml_bi.App19Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoSync
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.CambioEstadoOrdenRequest
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.MermaRequest
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.MermaRepository
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.ProduccionRepository
import com.google.gson.JsonParser

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as App19Application).container
        val dao = container.operacionPendienteDao
        val api = container.apiService
        val gson = container.gson

        var huboError = false

        // Envía en el mismo orden en que ocurrieron
        for (op in dao.obtenerPorEnviar()) {
            dao.actualizarEstado(op.uuid, EstadoSync.ENVIANDO, op.intentos, null)
            try {
                when (op.tipo) {
                    ProduccionRepository.TIPO_CAMBIO_ESTADO_ORDEN -> {
                        val json = JsonParser.parseString(op.payloadJson).asJsonObject
                        val ordenId = json.get("ordenId").asString
                        val request = gson.fromJson(json.get("request"), CambioEstadoOrdenRequest::class.java)
                        api.cambiarEstadoOrden(ordenId, request)
                    }
                    MermaRepository.TIPO_MERMA -> {
                        api.registrarMerma(gson.fromJson(op.payloadJson, MermaRequest::class.java))
                    }
                }
                dao.actualizarEstado(op.uuid, EstadoSync.SINCRONIZADO, op.intentos + 1, null)
            } catch (e: Exception) {
                dao.actualizarEstado(op.uuid, EstadoSync.ERROR, op.intentos + 1, e.message)
                huboError = true
            }
        }

        dao.limpiarSincronizadas()

        // retry → WorkManager lo vuelve a intentar más tarde (espera cada vez más)
        return if (huboError) Result.retry() else Result.success()
    }

    companion object {
        private const val NOMBRE = "sync_operaciones"

        // Lo llaman los repositorios (vía AppContainer) cada vez que hay algo nuevo en la cola
        fun programar(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED) // solo con internet
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                NOMBRE,
                ExistingWorkPolicy.APPEND_OR_REPLACE,  // si ya hay uno en curso, se encola detrás
                request
            )
        }
    }
}