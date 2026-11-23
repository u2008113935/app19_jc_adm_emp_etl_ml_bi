package com.example.app19_jc_adm_emp_etl_ml_bi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.app19_jc_adm_emp_etl_ml_bi.view.DeliciaApp
import com.example.app19_jc_adm_emp_etl_ml_bi.view.theme.App19_jc_adm_emp_etl_ml_biTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App19_jc_adm_emp_etl_ml_biTheme {
                DeliciaApp()
            }
        }
    }
}