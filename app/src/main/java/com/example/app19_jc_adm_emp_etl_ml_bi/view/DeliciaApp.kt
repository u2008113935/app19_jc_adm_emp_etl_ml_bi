package com.example.app19_jc_adm_emp_etl_ml_bi.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.app19_jc_adm_emp_etl_ml_bi.view.navigation.DeliciaNavGraph

@Composable
fun DeliciaApp() {
    val navController = rememberNavController()
    Scaffold { padding ->
        DeliciaNavGraph(navController, Modifier.padding(padding))
    }
}