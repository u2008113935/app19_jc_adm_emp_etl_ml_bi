package com.example.app19_jc_adm_emp_etl_ml_bi.view.empleado

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OrdenProduccionEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.EstadoOrdenChip
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeVacio
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.SyncBanner
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.empleado.ColaViewModel

// Cola del EMPLEADO: órdenes de hoy ordenadas por hora de entrega. Funciona sin señal.
@Composable
fun ColaPreparacionScreen(
    irADetalle: (String) -> Unit,
    viewModel: ColaViewModel = viewModel(factory = ColaViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        SyncBanner(estado.pendientesSync)
        if (estado.refrescando) LinearProgressIndicator(Modifier.fillMaxWidth())

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hoy ${estado.fecha}: ${estado.porHacer} por hacer", Modifier.weight(1f))
            TextButton(onClick = viewModel::refrescar) { Text("Actualizar") }
        }
        estado.aviso?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 16.dp))
        }

        if (estado.ordenes.isEmpty() && !estado.refrescando) MensajeVacio("No hay órdenes para hoy")

        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(estado.ordenes, key = { it.id }) { orden ->
                OrdenCard(orden, onClick = { irADetalle(orden.id) }, onAvanzar = { viewModel.avanzar(orden) })
            }
        }
    }
}

@Composable
private fun OrdenCard(orden: OrdenProduccionEntity, onClick: () -> Unit, onAvanzar: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(orden.horaEntrega, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "  ${orden.cantidad} x ${orden.productoNombre}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstadoOrdenChip(orden.estado)
                Text(
                    if (orden.pedidoId != null) "  Pedido de cliente" else "  Plan del día (ML)",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                when (orden.estado) {
                    EstadoOrden.PLANIFICADA -> Button(onClick = onAvanzar) { Text("Iniciar") }
                    EstadoOrden.EN_PROCESO -> Button(onClick = onAvanzar) { Text("Terminar") }
                    EstadoOrden.TERMINADA -> Unit
                }
            }
        }
    }
}
