package com.example.app19_jc_adm_emp_etl_ml_bi.model.repository

import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.dao.ProductoDao
import com.example.app19_jc_adm_emp_etl_ml_bi.model.local.entity.ProductoEntity
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.ApiService
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.ProductoDto
import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.ProductoRequest
import kotlinx.coroutines.flow.Flow

class ProductoRepository(
    private val api: ApiService,
    private val dao: ProductoDao
) {
    fun observarProductos(): Flow<List<ProductoEntity>> = dao.observarTodos()

    suspend fun buscarPorId(id: Int): ProductoEntity? = dao.buscarPorId(id)

    suspend fun refrescar(): Result<Unit> = runCatching {
        dao.guardarTodos(api.obtenerProductos().map { it.toEntity() })
    }

    // id == null → crear; id != null → actualizar
    suspend fun guardar(id: Int?, request: ProductoRequest): Result<Unit> = runCatching {
        val dto = if (id == null) api.crearProducto(request)
        else api.actualizarProducto(id, request)
        dao.guardarTodos(listOf(dto.toEntity()))
    }

    private fun ProductoDto.toEntity() = ProductoEntity(
        id = id,
        nombre = nombre,
        categoria = categoria,
        precio = precio,
        activo = activo,
        imagenUrl = imagenUrl
    )
}