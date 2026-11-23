package com.example.app19_jc_adm_emp_etl_ml_bi.model.local

import androidx.room.TypeConverter
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoSync

// SQLite solo guarda texto y números: aquí se traduce enum ↔ texto
class Converters {

    @TypeConverter fun fromEstadoPedido(valor: EstadoPedido): String = valor.name
    @TypeConverter fun toEstadoPedido(valor: String): EstadoPedido = EstadoPedido.valueOf(valor)

    @TypeConverter fun fromEstadoOrden(valor: EstadoOrden): String = valor.name
    @TypeConverter fun toEstadoOrden(valor: String): EstadoOrden = EstadoOrden.valueOf(valor)

    @TypeConverter fun fromEstadoSync(valor: EstadoSync): String = valor.name
    @TypeConverter fun toEstadoSync(valor: String): EstadoSync = EstadoSync.valueOf(valor)
}