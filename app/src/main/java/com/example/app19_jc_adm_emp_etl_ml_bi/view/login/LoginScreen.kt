package com.example.app19_jc_adm_emp_etl_ml_bi.view.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app19_jc_adm_emp_etl_ml_bi.R
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.Rol
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.login.LoginViewModel
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun LoginScreen(
    irAAdmin: () -> Unit,
    irAEmpleado: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Si el login fue correcto, navega según el rol
    LaunchedEffect(estado.rolIngresado) {
        when (estado.rolIngresado) {
            Rol.ADMIN -> irAAdmin()
            Rol.EMPLEADO -> irAEmpleado()
            null -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_delicia),
            contentDescription = "Logo Pastelería Delicia",
            modifier = Modifier.size(110.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Pastelería Delicia",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Ingreso de personal",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (estado.mostrarPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = viewModel::alternarMostrarPassword) {
                    Text(if (estado.mostrarPassword) "Ocultar" else "Ver")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        estado.error?.let { mensaje ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::ingresar,
            enabled = !estado.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Ingresar")
            }
        }
    }
}