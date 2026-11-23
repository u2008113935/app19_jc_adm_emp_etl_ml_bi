package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote

import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// Agrega "Authorization: Bearer <token>" a cada llamada, sin repetirlo en el ApiService
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // OkHttp ejecuta esto en un hilo de fondo, por eso runBlocking no congela la pantalla
        val token = runBlocking { sessionManager.obtenerToken() }

        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }
}