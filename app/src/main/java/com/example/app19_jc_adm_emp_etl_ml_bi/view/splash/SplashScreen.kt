package com.example.app19_jc_adm_emp_etl_ml_bi.view.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.R
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.splash.SplashDestino
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.splash.SplashViewModel

@Composable
fun SplashScreen(
    irALogin: () -> Unit,
    irAAdmin: () -> Unit,
    irAEmpleado: () -> Unit,
    viewModel: SplashViewModel = viewModel(factory = SplashViewModel.Factory)
) {
    val destino by viewModel.destino.collectAsStateWithLifecycle()

    // La View no decide: solo obedece lo que dice el ViewModel
    LaunchedEffect(destino) {
        when (destino) {
            SplashDestino.Login -> irALogin()
            SplashDestino.Admin -> irAAdmin()
            SplashDestino.Empleado -> irAEmpleado()
            SplashDestino.Cargando -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo en res/drawable/logo_delicia.png
        Image(
            painter = painterResource(id = R.drawable.logo_delicia),
            contentDescription = "Logo Pastelería Delicia",
            modifier = Modifier.size(140.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Pastelería Delicia",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Administración y Producción",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        CircularProgressIndicator()
    }
}