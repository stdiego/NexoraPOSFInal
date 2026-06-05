package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val stock: Int
)

@Serializable
data class ItemVenta(
    val nombreProducto: String,
    val precioUnitario: Double,
    val cantidad: Int
) {
    val subtotal: Double get() = precioUnitario * cantidad
}

@Serializable
data class Venta(
    val id: Int,
    val fecha: String,
    val items: List<ItemVenta>,
    val metodoPago: String,
    val total: Double
)

val jsonSerializer = Json { ignoreUnknownKeys = true }

fun List<Producto>.toJson(): String = jsonSerializer.encodeToString<List<Producto>>(this)
fun List<Venta>.toJsonVentas(): String = jsonSerializer.encodeToString<List<Venta>>(this)
fun String.toProductos(): List<Producto> =
    runCatching { if (isEmpty()) emptyList() else jsonSerializer.decodeFromString<List<Producto>>(this) }.getOrDefault(emptyList())
fun String.toVentas(): List<Venta> =
    runCatching { if (isEmpty()) emptyList() else jsonSerializer.decodeFromString<List<Venta>>(this) }.getOrDefault(emptyList())