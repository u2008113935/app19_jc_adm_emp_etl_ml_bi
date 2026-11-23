package com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // Flow: la pantalla se actualiza sola cuando cambia la tabla
    @Query("SELECT * FROM productos ORDER BY categoria, nombre")
    fun observarTodos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun buscarPorId(id: Int): ProductoEntity?

    // Upsert = inserta si no existe, actualiza si ya existe
    @Upsert
    suspend fun guardarTodos(productos: List<ProductoEntity>)
}