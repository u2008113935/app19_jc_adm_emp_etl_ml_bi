package com.example.app19_jc_adm_emp_etl_ml_bi.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.BarChart
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.KpiCard
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeError
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.Periodo
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.TableroViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableroScreen(
    viewModel: TableroViewModel = viewModel(factory = TableroViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Periodo.entries.forEach { p ->
                FilterChip(
                    selected = estado.periodo == p,
                    onClick = { viewModel.cambiarPeriodo(p) },
                    label = { Text(p.texto) }
                )
            }
        }
        if (estado.cargando) LinearProgressIndicator()
        estado.error?.let { MensajeError(it, onReintentar = viewModel::cargar) }

        estado.tablero?.let { t ->
            Text("Del ${t.desde} al ${t.hasta}", style = MaterialTheme.typography.bodySmall)

            // Tarjetas de 2 en 2
            t.kpis.chunked(2).forEach { par ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    par.forEach { kpi -> KpiCard(kpi, Modifier.weight(1f)) }
                    if (par.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            BarChart(
                titulo = "Ventas por día (S/)",
                datos = t.ventasPorDia,
                color = MaterialTheme.colorScheme.primary,
                formato = { String.format(Locale.US, "%.0f", it) }
            )
            BarChart(
                titulo = "Merma por producto (unidades)",
                datos = t.mermaPorProducto,
                color = Color(0xFFB3261E)
            )
        }
    }
}
