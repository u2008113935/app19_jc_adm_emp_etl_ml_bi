package com.example.app19_jc_adm_emp_etl_ml_bi.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.SerieDto
import java.util.Locale

// Barras horizontales: etiqueta | barra proporcional | valor
// Sin librerías: cada barra es un Box con ancho relativo al mayor valor
@Composable
fun BarChart(
    titulo: String,
    datos: List<SerieDto>,
    color: Color,
    modifier: Modifier = Modifier,
    formato: (Double) -> String = { String.format(Locale.US, "%.0f", it) }
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        if (datos.isEmpty()) {
            Text("Sin datos en este periodo", style = MaterialTheme.typography.bodySmall)
            return@Column
        }
        val maximo = datos.maxOf { it.valor }.takeIf { it > 0 } ?: 1.0

        datos.forEach { punto ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = punto.etiqueta,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(110.dp)
                )
                Box(Modifier.weight(1f).height(16.dp)) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((punto.valor / maximo).toFloat().coerceIn(0.01f, 1f))
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
                Text(
                    text = formato(punto.valor),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(72.dp)
                )
            }
        }
    }
}
