package com.example.app19_jc_adm_emp_etl_ml_bi

import android.app.Application
import com.example.app19_jc_adm_emp_etl_ml_bi.model.AppContainer
import com.example.app19_jc_adm_emp_etl_ml_bi.model.SyncWorker

class App19Application : Application() {

    // Un solo contenedor para toda la app; los ViewModels lo usan
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        // Al abrir la app, envía lo que haya quedado pendiente de antes
        SyncWorker.programar(this)
    }
}