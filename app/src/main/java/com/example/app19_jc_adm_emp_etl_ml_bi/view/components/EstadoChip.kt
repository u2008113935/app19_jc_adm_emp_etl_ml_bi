package com.example.app19_jc_adm_emp_etl_ml_bi.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido

// Etiqueta de color para un estado (fondo claro + texto oscuro del mismo tono)
@Composable
fun EstadoChip(texto: String, fondo: Color, letra: Color, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        color = letra,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun EstadoPedidoChip(estado: EstadoPedido) {
    val (texto, fondo, letra) = when (estado) {
        EstadoPedido.PEND_PAGO -> Triple("Pend. pago", Color(0xFFFFE9A8), Color(0xFF6B4A00))
        EstadoPedido.PAGADO -> Triple("Pagado", Color(0xFFE3D7FF), Color(0xFF3F2A86))
        EstadoPedido.EN_PREPARACION -> Triple("En preparación", Color(0xFFCFE8FF), Color(0xFF0B4A7A))
        EstadoPedido.LISTO -> Triple("Listo", Color(0xFFC8F7D8), Color(0xFF0B5A2A))
        EstadoPedido.ENTREGADO -> Triple("Entregado", Color(0xFFE6E6E6), Color(0xFF444444))
        EstadoPedido.CANCELADO -> Triple("Cancelado", Color(0xFFFFD6D2), Color(0xFF8A1C12))
    }
    EstadoChip(texto, fondo, letra)
}

@Composable
fun EstadoOrdenChip(estado: EstadoOrden) {
    val (texto, fondo, letra) = when (estado) {
        EstadoOrden.PLANIFICADA -> Triple("Por hacer", Color(0xFFFFE9A8), Color(0xFF6B4A00))
        EstadoOrden.EN_PROCESO -> Triple("En proceso", Color(0xFFCFE8FF), Color(0xFF0B4A7A))
        EstadoOrden.TERMINADA -> Triple("Terminada", Color(0xFFC8F7D8), Color(0xFF0B5A2A))
    }
    EstadoChip(texto, fondo, letra)
}

// Texto legible para los estados
fun EstadoPedido.etiqueta(): String = when (this) {
    EstadoPedido.PEND_PAGO -> "Pend. pago"
    EstadoPedido.PAGADO -> "Pagado"
    EstadoPedido.EN_PREPARACION -> "En preparación"
    EstadoPedido.LISTO -> "Listo"
    EstadoPedido.ENTREGADO -> "Entregado"
    EstadoPedido.CANCELADO -> "Cancelado"
}
