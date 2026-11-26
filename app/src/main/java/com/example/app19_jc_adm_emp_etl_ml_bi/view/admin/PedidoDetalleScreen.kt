package com.example.app19_jc_adm_emp_etl_ml_bi.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.Cargando
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.EstadoPedidoChip
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeError
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.AccionPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.PedidoDetalleViewModel
import java.util.Locale

@Composable
fun PedidoDetalleScreen(
    irACobroQr: (String) -> Unit,
    viewModel: PedidoDetalleViewModel = viewModel(factory = PedidoDetalleViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Se recarga cada vez que la pantalla vuelve a verse (por ejemplo, al regresar del cobro QR)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.cargar() }

    val pedido = estado.pedido
    when {
        estado.cargando && pedido == null -> Cargando()
        pedido == null -> MensajeError(estado.error ?: "No se encontró el pedido", onReintentar = viewModel::cargar)
        else -> Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cabecera
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(pedido.codigo, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                EstadoPedidoChip(pedido.estado)
            }
            Text("${pedido.clienteNombre} | ${pedido.clienteTelefono}")
            Text("Canal ${pedido.canal} | Entrega ${pedido.fechaEntrega} ${pedido.horaEntrega}")
            pedido.nota?.let { Text("Nota: $it", style = MaterialTheme.typography.bodySmall) }

            // Productos
            Card {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Productos", style = MaterialTheme.typography.titleMedium)
                    pedido.items.forEach { item ->
                        Row {
                            Text("${item.cantidad} x ${item.productoNombre}", Modifier.weight(1f))
                            Text(soles(item.subtotal))
                        }
                    }
                    HorizontalDivider()
                    Row {
                        Text("Total", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(soles(pedido.total), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Producción (lo que hace el empleado)
            if (pedido.ordenes.isNotEmpty()) {
                Card {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Producción", style = MaterialTheme.typography.titleMedium)
                        pedido.ordenes.forEach { Text("${it.cantidad} x ${it.productoNombre}: ${it.estado}") }
                    }
                }
            }

            // Pagos con Banco Andino
            pedido.pagos.firstOrNull()?.let { pago ->
                Text(
                    "Pago Banco Andino: ${pago.estado}" + (pago.nroOperacionBanco?.let { " | op. $it" } ?: ""),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Trazabilidad
            Card {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Historial", style = MaterialTheme.typography.titleMedium)
                    pedido.historial.forEach { h ->
                        Text(
                            "${h.fecha}  ${h.estadoNuevo}  ${h.usuario ?: "Sistema"}" + (h.nota?.let { " ($it)" } ?: ""),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            estado.error?.let { MensajeError(it) }
            estado.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

            // Acciones del admin
            if (pedido.estado == EstadoPedido.PEND_PAGO) {
                Button(
                    onClick = { irACobroQr(pedido.id) },
                    enabled = !estado.procesando,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cobrar con QR Banco Andino") }
            }
            viewModel.accionesDisponibles(pedido.estado).forEach { accion ->
                if (accion == AccionPedido.CANCELAR) {
                    OutlinedButton(
                        onClick = { viewModel.ejecutar(accion) },
                        enabled = !estado.procesando,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(accion.texto) }
                } else {
                    Button(
                        onClick = { viewModel.ejecutar(accion) },
                        enabled = !estado.procesando,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(accion.texto) }
                }
            }
            if (pedido.estado == EstadoPedido.PAGADO || pedido.estado == EstadoPedido.EN_PREPARACION) {
                Text(
                    "El empleado lo está preparando. Pasará a Listo cuando termine todas las órdenes.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun soles(valor: Double) = String.format(Locale.US, "S/ %.2f", valor)
