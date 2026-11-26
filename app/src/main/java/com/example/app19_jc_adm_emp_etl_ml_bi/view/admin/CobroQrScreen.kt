package com.example.app19_jc_adm_emp_etl_ml_bi.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.Cargando
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.MensajeError
import com.example.app19_jc_adm_emp_etl_ml_bi.view.components.QrImage
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.admin.CobroQrViewModel
import java.util.Locale

// El cliente escanea el QR con su app de Banco Andino; esta pantalla espera la confirmación
@Composable
fun CobroQrScreen(
    volver: () -> Unit,
    viewModel: CobroQrViewModel = viewModel(factory = CobroQrViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val qr = estado.qr

    when {
        estado.cargando -> Cargando()
        qr == null -> MensajeError(estado.error ?: "No se pudo generar el QR", onReintentar = viewModel::generar)
        else -> Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Pedido ${qr.referencia}", style = MaterialTheme.typography.titleLarge)
            Text(
                String.format(Locale.US, "S/ %.2f", qr.monto),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            when (estado.estadoPago) {
                "CONFIRMADO" -> {
                    Text("Pago confirmado", color = Color(0xFF1B7F3B), style = MaterialTheme.typography.headlineSmall)
                    estado.nroOperacion?.let { Text("Operación Banco Andino: $it") }
                    Text("El pedido pasó a PAGADO y ya está en la cola del empleado.", style = MaterialTheme.typography.bodySmall)
                    Button(onClick = volver, modifier = Modifier.fillMaxWidth()) { Text("Volver al pedido") }
                }
                "RECHAZADO" -> {
                    Text("Pago rechazado por el banco", color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::generar, modifier = Modifier.fillMaxWidth()) { Text("Generar otro QR") }
                }
                else -> {
                    QrImage(qr.qrTexto)
                    Text("Escanea con la app de Banco Andino", style = MaterialTheme.typography.bodyMedium)
                    CircularProgressIndicator()
                    Text("Esperando confirmación del banco...", style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = viewModel::simularPagoBanco, modifier = Modifier.fillMaxWidth()) {
                        Text("Simular pago del banco (demo)")
                    }
                }
            }
            estado.error?.let { MensajeError(it) }
        }
    }
}
