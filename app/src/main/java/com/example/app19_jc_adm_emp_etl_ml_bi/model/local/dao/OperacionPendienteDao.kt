package com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoSync
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.OperacionPendienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OperacionPendienteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(operacion: OperacionPendienteEntity)

    // Lo que el SyncWorker debe enviar, en el orden en que ocurrió
    @Query("SELECT * FROM operaciones_pendientes WHERE estado IN ('PENDIENTE','ERROR') ORDER BY creadoEn")
    suspend fun obtenerPorEnviar(): List<OperacionPendienteEntity>

    @Query("UPDATE operaciones_pendientes SET estado = :estado, intentos = :intentos, error = :error WHERE uuid = :uuid")
    suspend fun actualizarEstado(uuid: String, estado: EstadoSync, intentos: Int, error: String?)

    @Query("DELETE FROM operaciones_pendientes WHERE estado = 'SINCRONIZADO'")
    suspend fun limpiarSincronizadas()

    // Para la franja roja/verde (SyncBanner): cuántas faltan enviar
    @Query("SELECT COUNT(*) FROM operaciones_pendientes WHERE estado != 'SINCRONIZADO'")
    fun observarCantidadPendientes(): Flow<Int>
}