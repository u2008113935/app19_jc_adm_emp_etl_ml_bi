package com.example.app19_jc_adm_emp_etl_ml_bi.view.empleado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.SyncBanner
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.empleado.MOTIVOS_MERMA
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.empleado.MermaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MermaScreen(
    viewModel: MermaViewModel = viewModel(factory = MermaViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val f = estado.form

    Column(Modifier.fillMaxSize()) {
        SyncBanner(estado.pendientesSync)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Cierre del día: merma", style = MaterialTheme.typography.titleLarge)

            Text("Producto", style = MaterialTheme.typography.labelLarge)
            estado.productos.forEach { p ->
                Row(
                    Modifier.fillMaxWidth().selectable(
                        selected = f.productoId == p.id,
                        onClick = { viewModel.elegirProducto(p.id) }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = f.productoId == p.id, onClick = { viewModel.elegirProducto(p.id) })
                    Text(p.nombre)
                }
            }

            OutlinedTextField(
                value = f.cantidad,
                onValueChange = viewModel::onCantidad,
                label = { Text("Cantidad (unidades)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Motivo", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MOTIVOS_MERMA.forEach { m ->
                    FilterChip(
                        selected = f.motivo == m,
                        onClick = { viewModel.elegirMotivo(m) },
                        label = { Text(m.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            f.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            f.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

            Button(onClick = viewModel::registrar, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text("Registrar merma")
            }
        }
    }
}
