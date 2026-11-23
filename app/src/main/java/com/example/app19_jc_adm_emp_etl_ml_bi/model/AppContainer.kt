package com.example.app19_jc_adm_emp_etl_ml_bi.model

import android.content.Context
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.AppDatabase
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.SessionManager
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.OperacionPendienteDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.ApiService
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.NetworkModule
import com.example.app19_jc_adm_emp_etl_ml_bi.model.repository.*
import com.google.gson.Gson

class AppContainer(private val context: Context) {

    // ── Piezas base ──
    val gson = Gson()
    private val database = AppDatabase.obtener(context)
    val sessionManager = SessionManager(context)
    val apiService: ApiService = NetworkModule.crearApiService(sessionManager)
    val operacionPendienteDao: OperacionPendienteDao = database.operacionPendienteDao()

    // Función que se pasa a los repositorios offline-first
    private val programarSync: () -> Unit = { SyncWorker.programar(context) }

    // ── Repositorios (by lazy = se crean recién cuando alguien los usa) ──
    val authRepository by lazy { AuthRepository(apiService, sessionManager) }
    val pedidoRepository by lazy { PedidoRepository(apiService, database.pedidoDao()) }
    val productoRepository by lazy { ProductoRepository(apiService, database.productoDao()) }
    val pagoRepository by lazy { PagoRepository(apiService) }
    val mlRepository by lazy { MlRepository(apiService) }
    val analiticaRepository by lazy { AnaliticaRepository(apiService) }

    val produccionRepository by lazy {
        ProduccionRepository(
            api = apiService,
            ordenDao = database.ordenProduccionDao(),
            operacionDao = operacionPendienteDao,
            gson = gson,
            programarSync = programarSync
        )
    }

    val mermaRepository by lazy {
        MermaRepository(operacionPendienteDao, gson, programarSync)
    }
}