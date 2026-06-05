package org.example.project

import org.example.project.database.NexoraDatabase

class NexoraRepository(driverFactory: DatabaseDriverFactory) {

    private val database = NexoraDatabase(driverFactory.createDriver())
    private val queries  = database.nexoraDatabaseQueries

    init {
        // Poblar con productos de panadería si la BD está vacía
        if (queries.selectAllProductos().executeAsList().isEmpty()) {
            val productosPanaderia = listOf(
                Triple("Pan Francés",         300.0,  100),
                Triple("Pan de Queso",        500.0,   80),
                Triple("Croissant",          1200.0,   50),
                Triple("Pan Integral",        800.0,   60),
                Triple("Mogolla",             400.0,   90),
                Triple("Pan de Bono",         600.0,   70),
                Triple("Almojábana",          700.0,   60),
                Triple("Pandeyuca",           650.0,   75),
                Triple("Roscón de Guayaba", 2500.0,   30),
                Triple("Torta de Choclo",   3500.0,   20),
                Triple("Empanada de Pipián", 1000.0,  50),
                Triple("Buñuelo",             500.0,   80),
                Triple("Arepa de Choclo",    1500.0,   40),
                Triple("Galleta de Avena",    800.0,   60),
                Triple("Ponqué Tajado",      2000.0,   25)
            )
            productosPanaderia.forEach { (nombre, precio, stock) ->
                queries.insertProducto(nombre, precio, stock.toLong())
            }
        }
    }

    // ── Productos ──────────────────────────────────────────────────────────────

    fun obtenerProductos(): List<Producto> =
        queries.selectAllProductos().executeAsList().map {
            Producto(it.id.toInt(), it.nombre, it.precio, it.stock.toInt())
        }

    fun guardarProducto(nombre: String, precio: Double, stock: Int) {
        queries.insertProducto(nombre, precio, stock.toLong())
    }

    fun eliminarProducto(id: Int) {
        queries.deleteProducto(id.toLong())
    }

    fun actualizarStock(id: Int, nuevoStock: Int) {
        queries.updateStock(nuevoStock.toLong(), id.toLong())
    }

    // ── Ventas ─────────────────────────────────────────────────────────────────

    fun obtenerVentas(): List<Venta> {
        return queries.selectAllVentas().executeAsList().map { venta ->
            val items = queries.selectItemsByVentaId(venta.id).executeAsList().map { item ->
                ItemVenta(item.nombreProducto, item.precioUnitario, item.cantidad.toInt())
            }
            Venta(venta.id.toInt(), venta.fecha, items, venta.metodoPago, venta.total)
        }
    }

    fun guardarVenta(fecha: String, metodoPago: String, total: Double, items: List<ItemVenta>): Int {
        queries.insertVenta(fecha, metodoPago, total)
        val ventaId = queries.lastInsertRowId().executeAsOne()
        items.forEach { item ->
            queries.insertItemVenta(ventaId, item.nombreProducto, item.precioUnitario, item.cantidad.toLong())
        }
        return ventaId.toInt()
    }
}