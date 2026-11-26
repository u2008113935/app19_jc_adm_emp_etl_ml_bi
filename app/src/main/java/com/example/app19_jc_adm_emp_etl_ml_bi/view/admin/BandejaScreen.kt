package com.example.app19_jc_adm_emp_etl_ml_bi.view.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.PedidoEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.EstadoPedidoChip
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeError
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeVacio
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.etiqueta
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.BandejaViewModel
import java.util.Locale

// Bandeja del ADMIN: todos los pedidos (WhatsApp, mostrador, web, app) con filtro por estado
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaScreen(
    irADetalle: (String) -> Unit,
    viewModel: BandejaViewModel = viewModel(factory = BandejaViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        if (estado.refrescando) LinearProgressIndicator(Modifier.fillMaxWidth())

        // Chips de filtro: Todos + cada estado
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = estado.filtro == null,
                    onClick = { viewModel.seleccionarFiltro(null) },
                    label = { Text("Todos") }
                )
            }
            items(EstadoPedido.entries) { e ->
                FilterChip(
                    selected = estado.filtro == e,
                    onClick = { viewModel.seleccionarFiltro(e) },
                    label = { Text(e.etiqueta()) }
                )
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${estado.pedidos.size} pedido(s)", Modifier.weight(1f))
            TextButton(onClick = viewModel::refrescar) { Text("Actualizar") }
        }

        estado.error?.let { MensajeError(it, onReintentar = viewModel::refrescar) }

        if (estado.pedidos.isEmpty() && !estado.refrescando) {
            MensajeVacio("No hay pedidos con este filtro")
        }

        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(estado.pedidos, key = { it.id }) { pedido ->
                PedidoCard(pedido, onClick = { irADetalle(pedido.id) })
            }
        }
    }
}

@Composable
private fun PedidoCard(pedido: PedidoEntity, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pedido.codigo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EstadoPedidoChip(pedido.estado)
            }
            Text("${pedido.clienteNombre} | ${pedido.clienteTelefono}")
            Row {
                Text(
                    "Entrega ${pedido.fechaEntrega} ${pedido.horaEntrega} | ${pedido.canal}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    String.format(Locale.US, "S/ %.2f", pedido.total),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
