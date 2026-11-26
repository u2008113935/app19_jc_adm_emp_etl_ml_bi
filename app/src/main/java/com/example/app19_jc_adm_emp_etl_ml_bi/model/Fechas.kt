package com.example.app19_jc_adm_emp_etl_ml_bi.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Fechas en el formato de la API: "2026-11-23"
// (Calendar en vez de LocalDate porque minSdk = 24)
object Fechas {
    private fun formato() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun hoy(): String = desplazar(0)
    fun manana(): String = desplazar(1)

    // desplazar(-6) = hace 6 días
    fun desplazar(dias: Int): String {
        val calendario = Calendar.getInstance()
        calendario.add(Calendar.DAY_OF_YEAR, dias)
        return formato().format(calendario.time)
    }
}
