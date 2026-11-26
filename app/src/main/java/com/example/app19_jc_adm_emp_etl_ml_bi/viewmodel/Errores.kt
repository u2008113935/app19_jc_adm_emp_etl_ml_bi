package com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

// Traduce una excepción de red a un mensaje para el usuario.
// La API responde { "error": "mensaje" }: si existe, se muestra tal cual.
fun mensajeDeError(e: Throwable): String = when (e) {
    is HttpException -> leerErrorApi(e) ?: when (e.code()) {
        401 -> "Tu sesión venció. Vuelve a ingresar"
        403 -> "No tienes permiso para esta acción"
        else -> "Error del servidor (${e.code()})"
    }
    is IOException -> "Sin conexión con la API"
    else -> e.message ?: "Error inesperado"
}

private fun leerErrorApi(e: HttpException): String? = runCatching {
    val cuerpo = e.response()?.errorBody()?.string() ?: return null
    JsonParser.parseString(cuerpo).asJsonObject.get("error").asString
}.getOrNull()
