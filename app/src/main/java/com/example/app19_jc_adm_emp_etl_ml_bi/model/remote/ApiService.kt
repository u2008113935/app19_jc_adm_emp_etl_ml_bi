package com.example.app19_jc_adm_emp_etl_ml_bi.model.remote

import com.example.app19_jc_adm_emp_etl_ml_bi.model.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Contrato con la API Delicia: una función = una ruta
interface ApiService {

    // ── Autenticación ──
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // ── Productos (Admin) ──
    @GET("api/productos")
    suspend fun obtenerProductos(): List<ProductoDto>

    @POST("api/productos")
    suspend fun crearProducto(@Body request: ProductoRequest): ProductoDto

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Int, @Body request: ProductoRequest): ProductoDto

    // ── Pedidos (WhatsApp y mostrador) ──
    @GET("api/pedidos")
    suspend fun obtenerPedidos(
        @Query("estado") estado: String? = null,       // null = todos
        @Query("canal") canal: String? = null
    ): List<PedidoDto>

    @PATCH("api/pedidos/{id}/estado")
    suspend fun cambiarEstadoPedido(@Path("id") id: String, @Body request: CambioEstadoPedidoRequest): PedidoDto

    // ── Pagos → Banco Andino (la app nunca llama al banco directamente) ──
    @POST("api/pagos/qr")
    suspend fun generarQr(@Body request: QrRequest): QrResponse

    @GET("api/pagos/{id}")
    suspend fun consultarPago(@Path("id") pagoId: String): PagoEstadoDto

    // ── Producción (Empleado) ──
    @GET("api/produccion/ordenes")
    suspend fun obtenerOrdenes(@Query("fecha") fecha: String): List<OrdenDto>

    @PATCH("api/produccion/ordenes/{id}/estado")
    suspend fun cambiarEstadoOrden(@Path("id") id: String, @Body request: CambioEstadoOrdenRequest): OrdenDto

    @POST("api/produccion/plan")
    suspend fun aprobarPlan(@Body request: PlanRequest): List<OrdenDto>

    // ── Merma → alimenta el ETL ──
    @POST("api/merma")
    suspend fun registrarMerma(@Body request: MermaRequest)

    // ── ML ──
    @GET("api/ml/produccion-sugerida")
    suspend fun obtenerProduccionSugerida(@Query("fecha") fecha: String): List<ProduccionSugeridaDto>

    // ── Tableros (dw) ──
    @GET("api/kpis")
    suspend fun obtenerTablero(
        @Query("desde") desde: String,
        @Query("hasta") hasta: String
    ): TableroDto
}