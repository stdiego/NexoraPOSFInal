package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class NexoraState(
    val productos: List<Producto>   = emptyList(),
    val ventas: List<Venta>         = emptyList(),
    val carrito: Map<Producto, Int> = emptyMap()
) {
    val totalVentas: Double get() = ventas.sumOf { it.total }
    val totalTransacciones: Int get() = ventas.size
    val subtotalCarrito: Double get() = carrito.entries.sumOf { it.key.precio * it.value }
    val ivaCarrito: Double get() = subtotalCarrito * 0.16
    val granTotalCarrito: Double get() = subtotalCarrito + ivaCarrito
}

class NexoraViewModel(driverFactory: DatabaseDriverFactory) : ViewModel() {

    private val repository = NexoraRepository(driverFactory)

    var estado by mutableStateOf(NexoraState())
        private set

    init { cargarDatos() }

    fun cargarDatos() {
        estado = estado.copy(
            productos = repository.obtenerProductos(),
            ventas    = repository.obtenerVentas()
        )
    }

    fun agregarProducto(nombre: String, precio: Double, stock: Int) {
        repository.guardarProducto(nombre, precio, stock)
        cargarDatos()
    }

    fun eliminarProducto(id: Int) {
        val enCarrito = estado.carrito.keys.find { it.id == id }
        if (enCarrito != null) estado = estado.copy(carrito = estado.carrito - enCarrito)
        repository.eliminarProducto(id)
        cargarDatos()
    }

    fun agregarAlCarrito(producto: Producto) {
        val actual = estado.carrito[producto] ?: 0
        if (actual < producto.stock)
            estado = estado.copy(carrito = estado.carrito + (producto to actual + 1))
    }

    fun quitarDelCarrito(producto: Producto) {
        val actual = estado.carrito[producto] ?: 0
        estado = if (actual <= 1) estado.copy(carrito = estado.carrito - producto)
                 else estado.copy(carrito = estado.carrito + (producto to actual - 1))
    }

    fun limpiarCarrito() { estado = estado.copy(carrito = emptyMap()) }

    fun confirmarVenta(metodoPago: String, fecha: String): Venta? {
        if (estado.carrito.isEmpty()) return null
        estado.carrito.forEach { (producto, cantidad) ->
            repository.actualizarStock(producto.id, producto.stock - cantidad)
        }
        val items = estado.carrito.map { (p, c) -> ItemVenta(p.nombre, p.precio, c) }
        val ventaId = repository.guardarVenta(fecha, metodoPago, estado.subtotalCarrito, items)
        val venta = Venta(ventaId, fecha, items, metodoPago, estado.subtotalCarrito)
        estado = estado.copy(carrito = emptyMap())
        cargarDatos()
        return venta
    }
}
