package com.example.app19_jc_adm_emp_etl_ml_bi.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Franja roja: hay cambios guardados en el celular que aún no llegan a la API
// Franja verde: todo sincronizado
@Composable
fun SyncBanner(pendientes: Int, modifier: Modifier = Modifier) {
    val (texto, fondo) = if (pendientes > 0)
        "$pendientes cambio(s) por enviar. Se enviarán al tener señal" to Color(0xFFB3261E)
    else
        "Todo sincronizado con la API" to Color(0xFF1B7F3B)

    Text(
        text = texto,
        color = Color.White,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .fillMaxWidth()
            .background(fondo)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
