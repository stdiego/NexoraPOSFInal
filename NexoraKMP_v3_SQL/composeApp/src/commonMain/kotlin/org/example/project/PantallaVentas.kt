package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

class PantallaVentas : Screen {
    @Composable
    override fun Content() {
        val navigator    = LocalNavigator.currentOrThrow
        val vm: NexoraViewModel = viewModel { NexoraViewModel(createDriverFactory()) }
        val estado       = vm.estado
        var vista         by remember { mutableStateOf("CATALOGO") }
        var metodoPago    by remember { mutableStateOf("Efectivo") }
        var montoRecibido by remember { mutableStateOf("") }
        var errorMonto    by remember { mutableStateOf(false) }
        var ventaFinal    by remember { mutableStateOf<Venta?>(null) }

        LaunchedEffect(Unit) { vm.cargarDatos() }

        Column(modifier = Modifier.fillMaxSize().background(NexoraFondo)) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp)
                    .background(NexoraSuperficie)
                    .padding(horizontal = 4.dp, vertical = 12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        when (vista) {
                            "CATALOGO"     -> { vm.limpiarCarrito(); navigator.pop() }
                            "CARRITO"      -> vista = "CATALOGO"
                            "PAGO"         -> vista = "CARRITO"
                            "CONFIRMACION" -> navigator.pop()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = NexoraTextoPrimario)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            when (vista) {
                                "CATALOGO"     -> "Nueva Venta"
                                "CARRITO"      -> "Carrito"
                                "PAGO"         -> "Confirmar Pago"
                                else           -> "¡Completada!"
                            },
                            color = NexoraTextoPrimario, fontWeight = FontWeight.Bold, fontSize = 18.sp
                        )
                        if (vista == "CARRITO")
                            Text("${estado.carrito.values.sum()} ítem(s)", color = NexoraTextoSecundario, fontSize = 11.sp)
                    }
                }
            }

            when (vista) {
                "CATALOGO"     -> VistaCatalogoClaro(vm) { vista = "CARRITO" }
                "CARRITO"      -> VistaCarritoClaro(vm) { vista = "PAGO" }
                "PAGO"         -> VistaPagoClaro(
                    vm, metodoPago, { metodoPago = it },
                    montoRecibido, { montoRecibido = it; errorMonto = false },
                    errorMonto
                ) {
                    val recibido = montoRecibido.toDoubleOrNull() ?: 0.0
                    if (metodoPago == "Efectivo" && recibido < estado.granTotalCarrito) {
                        errorMonto = true
                    } else {
                        val now   = java.time.LocalDateTime.now()
                        val fecha = "${now.toLocalDate()} ${now.hour}:${now.minute.toString().padStart(2, '0')}"
                        ventaFinal = vm.confirmarVenta(metodoPago, fecha)
                        vista = "CONFIRMACION"
                    }
                }
                "CONFIRMACION" -> VistaConfirmacionClara(ventaFinal) { navigator.pop() }
            }
        }
    }
}

@Composable
fun VistaCatalogoClaro(vm: NexoraViewModel, onIrCarrito: () -> Unit) {
    val estado = vm.estado
    Column(modifier = Modifier.fillMaxSize()) {
        if (estado.productos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay productos. Agrega desde Inventario.", color = NexoraTextoSecundario, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier            = Modifier.weight(1f),
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(estado.productos, key = { it.id }) { producto ->
                    val enCarrito = estado.carrito[producto] ?: 0
                    val sinStock  = producto.stock == 0
                    val seleccionado = enCarrito > 0

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(if (seleccionado) 4.dp else 1.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(NexoraSuperficie)
                            .then(if (seleccionado) Modifier.border(1.5.dp, NexoraAzul.copy(alpha = 0.4f), RoundedCornerShape(16.dp)) else Modifier)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar inicial
                            Box(
                                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                                    .background(NexoraAzulClaro),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(producto.nombre.first().uppercase(), color = NexoraAzul, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(producto.nombre, color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("${"%.2f".format(producto.precio)}", color = NexoraAzul, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    if (sinStock) "Sin stock" else "Stock: ${producto.stock}",
                                    color = if (sinStock) NexoraRojo else NexoraTextoSecundario, fontSize = 11.sp
                                )
                                if (enCarrito > 0)
                                    Text("✓ $enCarrito en carrito", color = NexoraVerde, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            if (enCarrito > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(28.dp).clip(CircleShape).background(NexoraFondo),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(onClick = { vm.quitarDelCarrito(producto) }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Default.Remove, contentDescription = null, tint = NexoraTextoSecundario, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text("$enCarrito", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                    Box(
                                        Modifier.size(28.dp).clip(CircleShape).background(NexoraAzul),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(onClick = { vm.agregarAlCarrito(producto) }, enabled = !sinStock, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            } else {
                                Button(
                                    onClick   = { vm.agregarAlCarrito(producto) },
                                    enabled   = !sinStock,
                                    shape     = RoundedCornerShape(10.dp),
                                    colors    = ButtonDefaults.buttonColors(
                                        containerColor         = NexoraAzul,
                                        disabledContainerColor = NexoraFondo
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(0.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                    modifier  = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        if (sinStock) "Agotado" else "Agregar",
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        color = if (sinStock) NexoraTextoSecundario else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Botón ver carrito
            val hayItems = estado.carrito.isNotEmpty()
            Box(modifier = Modifier.fillMaxWidth().background(NexoraSuperficie).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Button(
                    onClick   = onIrCarrito,
                    enabled   = hayItems,
                    modifier  = Modifier.fillMaxWidth().height(54.dp),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = ButtonDefaults.buttonColors(
                        containerColor         = NexoraAzul,
                        disabledContainerColor = NexoraFondo
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null,
                        tint = if (hayItems) Color.White else NexoraTextoBajo,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (hayItems) "Ver Carrito  ·  ${"%.2f".format(estado.subtotalCarrito)}  (${estado.carrito.values.sum()})"
                        else "Carrito vacío",
                        color = if (hayItems) Color.White else NexoraTextoBajo,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun VistaCarritoClaro(vm: NexoraViewModel, onIrPago: () -> Unit) {
    val estado = vm.estado
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier            = Modifier.weight(1f),
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(estado.carrito.entries.toList(), key = { it.key.id }) { (producto, cantidad) ->
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
                            Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(NexoraTealClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(producto.nombre.first().uppercase(), color = NexoraTeal, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(producto.nombre, color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${"%.2f".format(producto.precio)} c/u", color = NexoraTextoSecundario, fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(28.dp).clip(CircleShape).background(NexoraFondo), contentAlignment = Alignment.Center) {
                                IconButton(onClick = { vm.quitarDelCarrito(producto) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = NexoraTextoSecundario, modifier = Modifier.size(14.dp))
                                }
                            }
                            Text("$cantidad", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp))
                            Box(Modifier.size(28.dp).clip(CircleShape).background(NexoraAzul), contentAlignment = Alignment.Center) {
                                IconButton(onClick = { vm.agregarAlCarrito(producto) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${"%.2f".format(producto.precio * cantidad)}", color = NexoraTextoPrimario, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            IconButton(onClick = { repeat(cantidad) { vm.quitarDelCarrito(producto) } }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Quitar", tint = NexoraRojo, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }

        // Panel total
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(NexoraSuperficie)
                .padding(20.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = NexoraTextoSecundario, fontSize = 13.sp)
                    Text("${"%.2f".format(estado.subtotalCarrito)}", color = NexoraTextoPrimario, fontSize = 13.sp)
                }
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("IVA (16%)", color = NexoraTextoSecundario, fontSize = 13.sp)
                    Text("${"%.2f".format(estado.ivaCarrito)}", color = NexoraTextoSecundario, fontSize = 13.sp)
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = NexoraBorde)
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${"%.2f".format(estado.granTotalCarrito)}", color = NexoraAzul, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick   = onIrPago,
                    modifier  = Modifier.fillMaxWidth().height(54.dp),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = ButtonDefaults.buttonColors(containerColor = NexoraAzul),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Proceder al Pago", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun VistaPagoClaro(
    vm: NexoraViewModel,
    metodoPago: String, onMetodo: (String) -> Unit,
    montoRecibido: String, onMonto: (String) -> Unit,
    errorMonto: Boolean,
    onConfirmar: () -> Unit
) {
    val estado   = vm.estado
    val recibido = montoRecibido.toDoubleOrNull() ?: 0.0
    val cambio   = maxOf(0.0, recibido - estado.granTotalCarrito)

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {

        // Total panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(NexoraAzul)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total a Pagar", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                Text("${"%.2f".format(estado.granTotalCarrito)}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp)
            }
        }

        Text("Método de Pago", color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Efectivo" to Icons.Default.Money, "Tarjeta" to Icons.Default.CreditCard, "Transfer" to Icons.Default.SwapHoriz).forEach { (metodo, icono) ->
                val sel = metodoPago == metodo
                val (color, fondo) = when (metodo) {
                    "Efectivo"  -> NexoraVerde to NexoraVerdeClaro
                    "Tarjeta"   -> NexoraAzul  to NexoraAzulClaro
                    else        -> NexoraMorado to NexoraMoradoClaro
                }
                Button(
                    onClick   = { onMetodo(metodo) },
                    modifier  = Modifier.weight(1f).height(60.dp),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = ButtonDefaults.buttonColors(
                        containerColor = if (sel) fondo else NexoraSuperficie
                    ),
                    border    = androidx.compose.foundation.BorderStroke(
                        1.5.dp, if (sel) color.copy(alpha = 0.5f) else NexoraBorde
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(icono, contentDescription = null, tint = if (sel) color else NexoraTextoBajo, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.height(3.dp))
                        Text(metodo, color = if (sel) color else NexoraTextoSecundario, fontSize = 10.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }

        if (metodoPago == "Efectivo") {
            Spacer(Modifier.height(20.dp))
            Text("Monto Recibido", color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = montoRecibido,
                onValueChange = onMonto,
                label         = { Text("0.00") },
                leadingIcon   = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = NexoraAzul, modifier = Modifier.size(18.dp)) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine    = true,
                isError       = errorMonto,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = NexoraAzul,
                    unfocusedBorderColor    = NexoraBorde,
                    focusedLabelColor       = NexoraAzul,
                    unfocusedLabelColor     = NexoraTextoSecundario,
                    focusedTextColor        = NexoraTextoPrimario,
                    unfocusedTextColor      = NexoraTextoPrimario,
                    cursorColor             = NexoraAzul,
                    focusedContainerColor   = NexoraSuperficie,
                    unfocusedContainerColor = NexoraFondo,
                    errorBorderColor        = NexoraRojo,
                    errorContainerColor     = NexoraRojoClaro
                )
            )
            if (errorMonto) Text("Monto insuficiente", color = NexoraRojo, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))

            if (recibido > 0) {
                Spacer(Modifier.height(12.dp))
                val positivo = recibido >= estado.granTotalCarrito
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (positivo) NexoraVerdeClaro else NexoraRojoClaro)
                        .border(1.dp, if (positivo) NexoraVerde.copy(alpha = 0.4f) else NexoraRojo.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cambio a devolver:", color = NexoraTextoSecundario, fontSize = 13.sp)
                        Text("${"%.2f".format(cambio)}", color = if (positivo) NexoraVerde else NexoraRojo, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick   = onConfirmar,
            modifier  = Modifier.fillMaxWidth().height(54.dp),
            shape     = RoundedCornerShape(16.dp),
            colors    = ButtonDefaults.buttonColors(containerColor = NexoraAzul),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Confirmar Venta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun VistaConfirmacionClara(venta: Venta?, onVolver: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(NexoraFondo).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(NexoraVerdeClaro),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexoraVerde, modifier = Modifier.size(60.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("¡Venta Completada!", color = NexoraTextoPrimario, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)

        venta?.let {
            Text("Venta #${it.id}  ·  ${it.fecha}", color = NexoraTextoSecundario, fontSize = 12.sp)
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(NexoraSuperficie)
                    .padding(16.dp)
            ) {
                Column {
                    it.items.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.nombreProducto} × ${item.cantidad}", color = NexoraTextoPrimario, fontSize = 13.sp)
                            Text("${"%.2f".format(item.subtotal)}", color = NexoraTextoSecundario, fontSize = 13.sp)
                        }
                    }
                    HorizontalDivider(color = NexoraBorde, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold)
                        Text("${"%.2f".format(it.total)}", color = NexoraAzul, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        Button(
            onClick   = onVolver,
            modifier  = Modifier.fillMaxWidth().height(54.dp),
            shape     = RoundedCornerShape(16.dp),
            colors    = ButtonDefaults.buttonColors(containerColor = NexoraAzul),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text("Volver al Inicio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// Mantener compatibilidad nombres anteriores
@Composable fun VistaCatalogo(vm: NexoraViewModel, onIrCarrito: () -> Unit) = VistaCatalogoClaro(vm, onIrCarrito)
@Composable fun VistaCarrito(vm: NexoraViewModel, onIrPago: () -> Unit) = VistaCarritoClaro(vm, onIrPago)
@Composable fun VistaPago(vm: NexoraViewModel, metodoPago: String, onMetodo: (String) -> Unit, montoRecibido: String, onMonto: (String) -> Unit, errorMonto: Boolean, onConfirmar: () -> Unit) = VistaPagoClaro(vm, metodoPago, onMetodo, montoRecibido, onMonto, errorMonto, onConfirmar)
@Composable fun VistaConfirmacion(venta: Venta?, onVolver: () -> Unit) = VistaConfirmacionClara(venta, onVolver)
