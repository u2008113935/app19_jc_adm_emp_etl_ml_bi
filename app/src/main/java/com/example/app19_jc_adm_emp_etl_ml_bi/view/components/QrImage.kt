package com.example.app19_jc_adm_emp_etl_ml_bi.view.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

// Dibuja el texto que manda el banco como imagen QR (librería zxing core)
@Composable
fun QrImage(texto: String, modifier: Modifier = Modifier) {
    val imagen = remember(texto) { generarQr(texto, 512).asImageBitmap() }
    Image(bitmap = imagen, contentDescription = "Código QR de pago", modifier = modifier.size(240.dp))
}

private fun generarQr(texto: String, tamano: Int): Bitmap {
    val matriz = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tamano, tamano)
    val pixeles = IntArray(tamano * tamano) { i ->
        if (matriz[i % tamano, i / tamano]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
    }
    return Bitmap.createBitmap(pixeles, tamano, tamano, Bitmap.Config.RGB_565)
}
