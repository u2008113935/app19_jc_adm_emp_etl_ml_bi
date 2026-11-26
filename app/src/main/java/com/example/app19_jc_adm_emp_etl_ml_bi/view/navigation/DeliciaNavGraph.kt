package com.example.app19_jc_adm_emp_etl_ml_bi.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.app19_jc_adm_emp_etl_ml_bi.view.admin.BandejaScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.admin.CobroQrScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.admin.PedidoDetalleScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.admin.PlanProduccionScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.admin.ProductoFormScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.admin.ProductosScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.admin.TableroScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.empleado.ColaPreparacionScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.empleado.MermaScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.empleado.OrdenDetalleScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.login.LoginScreen
import com.example.app19_jc_adm_emp_etl_ml_bi.view.splash.SplashScreen

@Composable
fun DeliciaNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    // Navega y borra el historial: con "Atrás" no se vuelve al Splash ni al Login
    fun irYLimpiar(ruta: String) = navController.navigate(ruta) {
        popUpTo(navController.graph.id) { inclusive = true }
    }
    val volver: () -> Unit = { navController.popBackStack() }

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
        composable(Rutas.BANDEJA) {
            BandejaScreen(irADetalle = { id -> navController.navigate(Rutas.pedidoDetalle(id)) })
        }
        composable(
            Rutas.PEDIDO_DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            PedidoDetalleScreen(irACobroQr = { id -> navController.navigate(Rutas.cobroQr(id)) })
        }
        composable(
            Rutas.COBRO_QR,
            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
        ) {
            CobroQrScreen(volver = volver)
        }
        composable(Rutas.PRODUCTOS) {
            ProductosScreen(irAFormulario = { id -> navController.navigate(Rutas.productoForm(id)) })
        }
        composable(
            Rutas.PRODUCTO_FORM,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            ProductoFormScreen(volver = volver)
        }
        composable(Rutas.PLAN) { PlanProduccionScreen() }
        composable(Rutas.TABLERO) { TableroScreen() }

        // ── Grafo EMPLEADO ──
        composable(Rutas.COLA) {
            ColaPreparacionScreen(irADetalle = { id -> navController.navigate(Rutas.ordenDetalle(id)) })
        }
        composable(
            Rutas.ORDEN_DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            OrdenDetalleScreen(volver = volver)
        }
        composable(Rutas.MERMA) { MermaScreen() }
    }
}
