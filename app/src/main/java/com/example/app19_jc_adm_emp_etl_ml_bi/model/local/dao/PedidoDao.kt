package com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.app19_jc_adm_emp_etl_ml_bi.model.enums.EstadoPedido
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    // Bandeja del admin: todos los pedidos
    @Query("SELECT * FROM pedidos ORDER BY fechaEntrega, horaEntrega")
    fun observarTodos(): Flow<List<PedidoEntity>>

    // Bandeja filtrada por estado (chips: Pend. pago, Pagado, Listo...)
    @Query("SELECT * FROM pedidos WHERE estado = :estado ORDER BY fechaEntrega, horaEntrega")
    fun observarPorEstado(estado: EstadoPedido): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE id = :id")
    suspend fun buscarPorId(id: String): PedidoEntity?

    @Upsert
    suspend fun guardarTodos(pedidos: List<PedidoEntity>)

    @Query("UPDATE pedidos SET estado = :estado, actualizadoEn = :ahora WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: EstadoPedido, ahora: Long = System.currentTimeMillis())
}