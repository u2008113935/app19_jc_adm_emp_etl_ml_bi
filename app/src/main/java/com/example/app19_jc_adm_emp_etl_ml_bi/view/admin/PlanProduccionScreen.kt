package com.example.app19_jc_adm_emp_etl_ml_bi.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.model.Fechas
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.Cargando
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeError
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeVacio
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.FilaPlan
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.PlanProduccionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanProduccionScreen(
    viewModel: PlanProduccionViewModel = viewModel(factory = PlanProduccionViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        Text(
            "El modelo sugiere cuánto producir. Ajusta y aprueba.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = estado.fecha == Fechas.hoy(),
                onClick = { viewModel.cambiarFecha(Fechas.hoy()) },
                label = { Text("Hoy") }
            )
            FilterChip(
                selected = estado.fecha == Fechas.manana(),
                onClick = { viewModel.cambiarFecha(Fechas.manana()) },
                label = { Text("Mañana") }
            )
        }

        when {
            estado.cargando -> Cargando()
            estado.filas.isEmpty() && estado.error == null ->
                MensajeVacio("Aún no hay pronóstico para ${estado.fecha}. Ejecuta el notebook de Colab.")
            else -> {
                LazyColumn(
                    Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(estado.filas, key = { it.sugerencia.productoId }) { fila ->
                        FilaPlanCard(fila, onCantidad = { viewModel.cambiarCantidad(fila.sugerencia.productoId, it) })
                    }
                }
                estado.error?.let { MensajeError(it) }
                estado.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                Button(
                    onClick = viewModel::aprobar,
                    enabled = !estado.aprobando && estado.filas.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(50.dp)
                ) { Text("Aprobar plan") }
            }
        }
    }
}

@Composable
private fun FilaPlanCard(fila: FilaPlan, onCantidad: (String) -> Unit) {
    val s = fila.sugerencia
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(s.productoNombre, fontWeight = FontWeight.Bold)
                Text(
                    "Sugerido ${s.cantidadSugerida} (rango ${s.cantidadMin} a ${s.cantidadMax})",
                    style = MaterialTheme.typography.bodySmall
                )
                Text("Modelo ${s.modeloVersion}", style = MaterialTheme.typography.labelSmall)
            }
            OutlinedTextField(
                value = fila.cantidad,
                onValueChange = onCantidad,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(84.dp)
            )
        }
    }
}
