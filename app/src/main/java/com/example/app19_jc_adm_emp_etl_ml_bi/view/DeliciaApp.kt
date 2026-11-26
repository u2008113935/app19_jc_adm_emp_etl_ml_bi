package com.example.app19_jc_adm_emp_etl_ml_bi.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app19_jc_adm_emp_etl_ml_bi.view.navigation.DeliciaNavGraph
import com.example.app19_jc_adm_emp_etl_ml_bi.view.navigation.Rutas
import com.example.app19_jc_adm_emp_etl_ml_bi.viewmodel.sesion.SesionViewModel

// Pestañas de la barra inferior según el rol
private data class Pestana(val ruta: String, val texto: String, val icono: ImageVector)

private val PESTANAS_ADMIN = listOf(
    Pestana(Rutas.BANDEJA, "Pedidos", Icons.AutoMirrored.Filled.List),
    Pestana(Rutas.PRODUCTOS, "Productos", Icons.Filled.ShoppingCart),
    Pestana(Rutas.PLAN, "Plan ML", Icons.Filled.DateRange),
    Pestana(Rutas.TABLERO, "Tablero", Icons.Filled.Info)
)
private val PESTANAS_EMPLEADO = listOf(
    Pestana(Rutas.COLA, "Cola", Icons.Filled.Build),
    Pestana(Rutas.MERMA, "Merma", Icons.Filled.Delete)
)

private val TITULOS = mapOf(
    Rutas.BANDEJA to "Bandeja de pedidos",
    Rutas.PEDIDO_DETALLE to "Detalle del pedido",
    Rutas.COBRO_QR to "Cobro con QR",
    Rutas.PRODUCTOS to "Productos",
    Rutas.PRODUCTO_FORM to "Producto",
    Rutas.PLAN to "Plan de producción",
    Rutas.TABLERO to "Tablero",
    Rutas.COLA to "Cola de preparación",
    Rutas.ORDEN_DETALLE to "Orden de producción",
    Rutas.MERMA to "Merma"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliciaApp(sesionViewModel: SesionViewModel = viewModel(factory = SesionViewModel.Factory)) {
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val nombre by sesionViewModel.nombre.collectAsStateWithLifecycle()

    val pestanas = when {
        ruta?.startsWith("admin/") == true -> PESTANAS_ADMIN
        ruta?.startsWith("empleado/") == true -> PESTANAS_EMPLEADO
        else -> emptyList()                                  // splash y login: sin barras
    }
    val esPrincipal = pestanas.any { it.ruta == ruta }

    Scaffold(
        topBar = {
            if (pestanas.isNotEmpty()) {
                TopAppBar(
                    title = { Text(TITULOS[ruta] ?: "Delicia") },
                    navigationIcon = {
                        if (!esPrincipal) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    },
                    actions = {
                        nombre?.let { Text(it.substringBefore(' ')) }
                        IconButton(onClick = {
                            sesionViewModel.cerrarSesion { irALogin(navController) }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (esPrincipal) {
                NavigationBar {
                    pestanas.forEach { p ->
                        NavigationBarItem(
                            selected = ruta == p.ruta,
                            onClick = {
                                navController.navigate(p.ruta) {
                                    popUpTo(pestanas.first().ruta) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(p.icono, contentDescription = null) },
                            label = { Text(p.texto) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        DeliciaNavGraph(navController, Modifier.padding(padding))
    }
}

private fun irALogin(navController: NavHostController) {
    navController.navigate(Rutas.LOGIN) {
        popUpTo(navController.graph.id) { inclusive = true }
    }
}
