package com.example.app19_jc_adm_emp_etl_ml_bi.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.OperacionPendienteDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.OrdenProduccionDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.PedidoDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.ProductoDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OperacionPendienteEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OrdenProduccionEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.PedidoEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.ProductoEntity

@Database(
    entities = [
        ProductoEntity::class,
        PedidoEntity::class,
        OrdenProduccionEntity::class,
        OperacionPendienteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun ordenProduccionDao(): OrdenProduccionDao
    abstract fun operacionPendienteDao(): OperacionPendienteDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        // Una sola base de datos para toda la app (patrón Singleton)
        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "delicia_adm.db")
                    .fallbackToDestructiveMigration(dropAllTables = true) // solo para clase: si cambias una entidad, recrea la BD
                    .build()
                    .also { instancia = it }
            }
    }
}