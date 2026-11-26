package com.example.app19_jc_adm_emp_etl_ml_bi.view.empleado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.Cargando
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.EstadoOrdenChip
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.empleado.OrdenDetalleViewModel

@Composable
fun OrdenDetalleScreen(
    volver: () -> Unit,
    viewModel: OrdenDetalleViewModel = viewModel(factory = OrdenDetalleViewModel.Factory)
) {
    val orden by viewModel.orden.collectAsStateWithLifecycle()
    val o = orden ?: return Cargando()

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(o.productoNombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Cantidad: ${o.cantidad}", style = MaterialTheme.typography.titleLarge)
        Text("Entrega: ${o.fechaProduccion} a las ${o.horaEntrega}")
        Text(if (o.pedidoId != null) "Origen: pedido de cliente" else "Origen: plan del día sugerido por el ML")
        EstadoOrdenChip(o.estado)

        when (o.estado) {
            EstadoOrden.PLANIFICADA -> Button(
                onClick = { viewModel.cambiarEstado(EstadoOrden.EN_PROCESO) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text("Iniciar preparación") }

            EstadoOrden.EN_PROCESO -> Button(
                onClick = { viewModel.cambiarEstado(EstadoOrden.TERMINADA) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text("Marcar terminada") }

            EstadoOrden.TERMINADA -> Button(onClick = volver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver a la cola")
            }
        }
        Text(
            "Puedes marcar sin señal: el cambio se guarda en el celular y se envía después.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
