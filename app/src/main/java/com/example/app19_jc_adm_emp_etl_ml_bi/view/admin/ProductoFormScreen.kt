package com.example.app19_jc_adm_emp_etl_ml_bi.view.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.CATEGORIAS
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.ProductoFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    volver: () -> Unit,
    viewModel: ProductoFormViewModel = viewModel(factory = ProductoFormViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.guardado) { if (estado.guardado) volver() }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(if (estado.esNuevo) "Nuevo producto" else "Editar producto", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onNombre,
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Categoría", style = MaterialTheme.typography.labelLarge)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CATEGORIAS.forEach { c ->
                FilterChip(
                    selected = estado.categoria == c,
                    onClick = { viewModel.onCategoria(c) },
                    label = { Text(c.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        OutlinedTextField(
            value = estado.precio,
            onValueChange = viewModel::onPrecio,
            label = { Text("Precio (S/)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Activo en el catálogo", Modifier.weight(1f))
            Switch(checked = estado.activo, onCheckedChange = viewModel::onActivo)
        }

        estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = viewModel::guardar,
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (estado.guardando) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Guardar")
        }
    }
}
