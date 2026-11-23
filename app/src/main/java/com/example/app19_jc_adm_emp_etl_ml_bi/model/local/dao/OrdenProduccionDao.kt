package com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoOrden
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OrdenProduccionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdenProduccionDao {

    // Cola del empleado: las de un día, ordenadas por hora de entrega
    @Query("SELECT * FROM ordenes_produccion WHERE fechaProduccion = :fecha ORDER BY horaEntrega")
    fun observarCola(fecha: String): Flow<List<OrdenProduccionEntity>>

    @Query("SELECT * FROM ordenes_produccion WHERE id = :id")
    suspend fun buscarPorId(id: String): OrdenProduccionEntity?

    @Upsert
    suspend fun guardarTodas(ordenes: List<OrdenProduccionEntity>)

    // Se ejecuta primero en local (funciona sin señal); luego el SyncWorker avisa a la API
    @Query("UPDATE ordenes_produccion SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: EstadoOrden)
}