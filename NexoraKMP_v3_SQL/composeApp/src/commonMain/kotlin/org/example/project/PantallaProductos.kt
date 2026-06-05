package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class PantallaProductos : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: NexoraViewModel = viewModel { NexoraViewModel(createDriverFactory()) }
        val estado = vm.estado

        LaunchedEffect(Unit) { vm.cargarDatos() }

        Column(modifier = Modifier.fillMaxSize().background(NexoraFondo)) {
            // Top bar
            Box(
                modifier = Modifier.fillMaxWidth().shadow(4.dp).background(NexoraSuperficie)
                    .padding(horizontal = 4.dp, vertical = 12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = NexoraTextoPrimario)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Inventario", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${estado.productos.size} productos", color = NexoraTextoSecundario, fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(NexoraAzulClaro),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { navigator.push(PantallaNuevoProducto()) }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar", tint = NexoraAzul)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }

            if (estado.productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(NexoraAmarClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = null, tint = NexoraAmarillo, modifier = Modifier.size(40.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Sin productos todavía", color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Agrega tu primer producto\npara comenzar a vender", color = NexoraTextoSecundario, fontSize = 13.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { navigator.push(PantallaNuevoProducto()) },
                            colors  = ButtonDefaults.buttonColors(containerColor = NexoraAzul),
                            shape   = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Agregar Producto", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(estado.productos, key = { it.id }) { producto ->
                        var confirmar by remember { mutableStateOf(false) }

                        val (stockColor, stockFondo, stockLabel) = when {
                            producto.stock == 0 -> Triple(NexoraRojo,    NexoraRojoClaro,   "Sin stock")
                            producto.stock < 5  -> Triple(NexoraOro,     NexoraOroClaro,    "Stock bajo")
                            else                -> Triple(NexoraVerde,   NexoraVerdeClaro,  "En stock")
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(NexoraSuperficie)
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(NexoraMoradoClaro),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(producto.nombre.first().uppercase(), color = NexoraMorado, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(producto.nombre, color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("${"%.2f".format(producto.precio)}", color = NexoraAzul, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(stockFondo)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("${producto.stock} uds  ·  $stockLabel", color = stockColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Box(
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(NexoraRojoClaro),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(onClick = { confirmar = true }, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = NexoraRojo, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        if (confirmar) {
                            AlertDialog(
                                onDismissRequest = { confirmar = false },
                                containerColor   = NexoraSuperficie,
                                shape            = RoundedCornerShape(20.dp),
                                title = { Text("Eliminar producto", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold) },
                                text  = { Text("¿Eliminar «${producto.nombre}»? Esta acción no se puede deshacer.", color = NexoraTextoSecundario) },
                                confirmButton = {
                                    TextButton(onClick = { vm.eliminarProducto(producto.id); confirmar = false }) {
                                        Text("Eliminar", color = NexoraRojo, fontWeight = FontWeight.Bold)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { confirmar = false }) {
                                        Text("Cancelar", color = NexoraTextoSecundario)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

class PantallaNuevoProducto : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: NexoraViewModel = viewModel { NexoraViewModel(createDriverFactory()) }
        var nombre by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }
        var stock  by remember { mutableStateOf("") }
        var error  by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize().background(NexoraFondo)) {
            Box(
                modifier = Modifier.fillMaxWidth().shadow(4.dp).background(NexoraSuperficie)
                    .padding(horizontal = 4.dp, vertical = 12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = NexoraTextoPrimario)
                    }
                    Text("Nuevo Producto", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CampoClaro(nombre, { nombre = it; error = "" }, "Nombre del producto", icono = Icons.Default.Label)
                CampoClaro(precio, { precio = it; error = "" }, "Precio (\$)", icono = Icons.Default.AttachMoney,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number))
                CampoClaro(stock, { stock = it; error = "" }, "Cantidad en stock", icono = Icons.Default.Inventory2,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number))

                if (error.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(NexoraRojoClaro).border(1.dp, NexoraRojo.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(error, color = NexoraRojo, fontSize = 13.sp)
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth().background(NexoraSuperficie).padding(horizontal = 20.dp, vertical = 16.dp)) {
                Button(
                    onClick = {
                        val p = precio.toDoubleOrNull(); val s = stock.toIntOrNull()
                        when {
                            nombre.isBlank()    -> error = "El nombre no puede estar vacío"
                            p == null || p <= 0 -> error = "Precio inválido"
                            s == null || s < 0  -> error = "Stock inválido"
                            else                -> { vm.agregarProducto(nombre.trim(), p, s); navigator.pop() }
                        }
                    },
                    modifier  = Modifier.fillMaxWidth().height(54.dp),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = ButtonDefaults.buttonColors(containerColor = NexoraAzul),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Guardar Producto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun CampoNexora(valor: String, onCambio: (String) -> Unit, etiqueta: String) =
    CampoClaro(valor, onCambio, etiqueta)
