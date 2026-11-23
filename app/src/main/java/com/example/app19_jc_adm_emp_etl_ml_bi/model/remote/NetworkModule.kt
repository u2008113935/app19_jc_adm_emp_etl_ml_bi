package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote

import com.example.app19_jc_adm_emp_etl_ml_bi.BuildConfig
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Arma Retrofit una sola vez; lo usa el AppContainer
object NetworkModule {

    fun crearApiService(sessionManager: SessionManager): ApiService {

        // Muestra en Logcat cada petición y respuesta (útil en clase)
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        val cliente = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)              // http://10.0.2.2:8000/
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}