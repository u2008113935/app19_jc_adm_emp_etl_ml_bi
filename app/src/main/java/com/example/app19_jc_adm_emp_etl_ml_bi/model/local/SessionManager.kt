package com.example.app19_jc_adm_emp_etl_ml_bi.model.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Un solo archivo de preferencias para toda la app
private val Context.dataStore by preferencesDataStore(name = "sesion")

class SessionManager(private val context: Context) {

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_ROL = stringPreferencesKey("rol")
        val KEY_NOMBRE = stringPreferencesKey("nombre")
    }

    // Flows: el Splash los observa para decidir a qué pantalla ir
    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }

    val rol: Flow<Rol?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ROL]?.let { valor -> runCatching { Rol.valueOf(valor) }.getOrNull() }
    }

    val nombre: Flow<String?> = context.dataStore.data.map { it[KEY_NOMBRE] }

    // Se llama después de un login correcto
    suspend fun guardarSesion(token: String, rol: Rol, nombre: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_ROL] = rol.name
            prefs[KEY_NOMBRE] = nombre
        }
    }

    // Lectura puntual del token (la usa AuthInterceptor en cada llamada a la API)
    suspend fun obtenerToken(): String? = token.first()

    suspend fun cerrarSesion() {
        context.dataStore.edit { it.clear() }
    }
}