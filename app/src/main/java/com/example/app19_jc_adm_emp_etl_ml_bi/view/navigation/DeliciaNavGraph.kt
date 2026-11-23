package com.example.app19_jc_adm_emp_etl_ml_bi.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app19_jc_adm_emp_etl_ml_bi.view.admin.BandejaScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.empleado.ColaPreparacionScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.login.LoginScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.splash.SplashScreen

@Composable
fun DeliciaNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    // Navega y borra el historial: con "Atrás" no se vuelve al Splash ni al Login
    fun irYLimpiar(ruta: String) = navController.navigate(ruta) {
        popUpTo(navController.graph.id) { inclusive = true }
    }

    NavHost(navController = navController, startDestination = Rutas.SPLASH, modifier = modifier) {

        composable(Rutas.SPLASH) {
            SplashScreen(
                irALogin = { irYLimpiar(Rutas.LOGIN) },
                irAAdmin = { irYLimpiar(Rutas.BANDEJA) },
                irAEmpleado = { irYLimpiar(Rutas.COLA) }
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                irAAdmin = { irYLimpiar(Rutas.BANDEJA) },
                irAEmpleado = { irYLimpiar(Rutas.COLA) }
            )
        }

        // ── Grafo ADMIN ──
        composable(Rutas.BANDEJA) { BandejaScreen() }

        // ── Grafo EMPLEADO ──
        composable(Rutas.COLA) { ColaPreparacionScreen() }
    }
}