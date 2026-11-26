package com.example.app19_jc_adm_emp_etl_ml_bi.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.KpiDto
import java.util.Locale

// Tarjeta de un indicador: nombre, valor con unidad y variación vs. periodo anterior
@Composable
fun KpiCard(kpi: KpiDto, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(kpi.nombre, style = MaterialTheme.typography.labelLarge)
            Text(
                text = formatearValor(kpi.valor, kpi.unidad),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            kpi.variacion?.let { v ->
                // En la merma subir es malo: el color se invierte
                val bueno = if (kpi.nombre.contains("Merma")) v <= 0 else v >= 0
                Text(
                    text = String.format(Locale.US, "%s%.1f %% vs. periodo anterior", if (v >= 0) "+" else "", v),
                    color = if (bueno) Color(0xFF1B7F3B) else Color(0xFFB3261E),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

fun formatearValor(valor: Double, unidad: String): String = when (unidad) {
    "S/" -> String.format(Locale.US, "S/ %,.2f", valor)
    "%" -> String.format(Locale.US, "%.1f %%", valor)
    else -> String.format(Locale.US, "%.0f %s", valor, unidad)
}
