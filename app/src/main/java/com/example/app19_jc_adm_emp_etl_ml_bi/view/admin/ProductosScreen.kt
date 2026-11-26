package com.example.app19_jc_adm_emp_etl_ml_bi.view.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.ProductoEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeError
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.ProductosViewModel
import java.util.Locale

@Composable
fun ProductosScreen(
    irAFormulario: (Int?) -> Unit,                  // null = nuevo
    viewModel: ProductosViewModel = viewModel(factory = ProductosViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            if (estado.refrescando) LinearProgressIndicator(Modifier.fillMaxWidth())
            estado.error?.let { MensajeError(it, onReintentar = viewModel::refrescar) }

            LazyColumn(
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.productos, key = { it.id }) { producto ->
                    ProductoCard(producto, onClick = { irAFormulario(producto.id) })
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { irAFormulario(null) },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("Nuevo") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}

@Composable
private fun ProductoCard(producto: ProductoEntity, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick).alpha(if (producto.activo) 1f else 0.5f)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    producto.categoria + if (producto.activo) "" else " | inactivo",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(String.format(Locale.US, "S/ %.2f", producto.precio), fontWeight = FontWeight.Bold)
        }
    }
}
