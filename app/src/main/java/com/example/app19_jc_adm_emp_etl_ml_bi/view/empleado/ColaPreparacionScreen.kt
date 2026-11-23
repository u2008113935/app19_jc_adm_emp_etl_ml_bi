package com.example.app19_jc_adm_emp_etl_ml_bi.view.empleado

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.app19_jc_adm_emp_etl_ml_bi.R

// Temporal: en el siguiente paso mostrará las órdenes de producción
@Composable
fun ColaPreparacionScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_cola_preparacion),
            contentDescription = "Cola de preparación",
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Cola de preparación",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Rol EMPLEADO",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}