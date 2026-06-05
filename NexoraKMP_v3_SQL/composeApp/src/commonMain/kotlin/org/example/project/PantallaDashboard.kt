package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class PantallaDashboard : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: NexoraViewModel = viewModel { NexoraViewModel(createDriverFactory()) }
        val estado = vm.estado

        LaunchedEffect(Unit) { vm.cargarDatos() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NexoraFondo)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top bar blanca ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp)
                    .background(NexoraSuperficie)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("¡Hola, bienvenido!", color = NexoraTextoSecundario, fontSize = 12.sp)
                        Text("Nexora POS", color = NexoraTextoPrimario, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NexoraAzul),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("N", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Banner hero — fondo azul plano (es panel, no card de datos) ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NexoraAzul)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dashboard", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                        Text("Resumen en\ntiempo real", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, lineHeight = 28.sp)
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Ver detalles →", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Métricas — tres cards en fila / columna con colores vibrantes ──
            Text("Resumen General", color = NexoraTextoSecundario, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CardMetrica(
                    modifier  = Modifier.weight(1f),
                    icono     = Icons.Default.ShoppingCart,
                    iconoColor = NexoraAzul,
                    fondoIcono = NexoraAzulClaro,
                    label     = "Ventas",
                    valor     = "${"%.0f".format(estado.totalVentas)}"
                )
                CardMetrica(
                    modifier  = Modifier.weight(1f),
                    icono     = Icons.Default.Receipt,
                    iconoColor = NexoraMorado,
                    fondoIcono = NexoraMoradoClaro,
                    label     = "Transacciones",
                    valor     = "${estado.totalTransacciones}"
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CardMetrica(
                    modifier  = Modifier.weight(1f),
                    icono     = Icons.Default.Inventory,
                    iconoColor = NexoraAmarillo,
                    fondoIcono = NexoraAmarClaro,
                    label     = "Productos",
                    valor     = "${estado.productos.size}"
                )
                CardMetrica(
                    modifier  = Modifier.weight(1f),
                    icono     = Icons.Default.TrendingUp,
                    iconoColor = NexoraTeal,
                    fondoIcono = NexoraTealClaro,
                    label     = "Hoy",
                    valor     = "${"%.0f".format(estado.ventas.filter { true }.sumOf { it.total })}"
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Acciones rápidas ───────────────────────────────────────────
            Text("Acciones Rápidas", color = NexoraTextoSecundario, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonAccionClaro(
                    modifier   = Modifier.weight(1f),
                    texto      = "Nueva Venta",
                    icono      = Icons.Default.PointOfSale,
                    color      = NexoraAzul,
                    fondoClaro = NexoraAzulClaro
                ) { navigator.push(PantallaVentas()) }

                BotonAccionClaro(
                    modifier   = Modifier.weight(1f),
                    texto      = "Inventario",
                    icono      = Icons.Default.Inventory,
                    color      = NexoraAmarillo,
                    fondoClaro = NexoraAmarClaro
                ) { navigator.push(PantallaProductos()) }
            }
            Spacer(Modifier.height(12.dp))
            BotonAccionClaro(
                modifier   = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                texto      = "Acerca de / Créditos",
                icono      = Icons.Default.Info,
                color      = NexoraMorado,
                fondoClaro = NexoraMoradoClaro,
                alto       = 70
            ) { navigator.push(PantallaCreditos()) }

            // ── Últimas ventas ─────────────────────────────────────────────
            if (estado.ventas.isNotEmpty()) {
                Spacer(Modifier.height(28.dp))
                Text("Últimas Ventas", color = NexoraTextoSecundario, fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(12.dp))

                estado.ventas.takeLast(3).reversed().forEach { venta ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(NexoraSuperficie)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(NexoraTealClaro),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = NexoraTeal, modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Venta #${venta.id}", color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("${venta.fecha}  ·  ${venta.metodoPago}", color = NexoraTextoSecundario, fontSize = 11.sp)
                                }
                            }
                            Text(
                                "${"%.2f".format(venta.total)}",
                                color      = NexoraAzul,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 16.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun CardMetrica(
    modifier: Modifier = Modifier,
    icono: ImageVector,
    iconoColor: Color,
    fondoIcono: Color,
    label: String,
    valor: String
) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(NexoraSuperficie)
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(fondoIcono),
                contentAlignment = Alignment.Center
            ) { Icon(icono, contentDescription = null, tint = iconoColor, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.height(10.dp))
            Text(valor, color = NexoraTextoPrimario, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(label, color = NexoraTextoSecundario, fontSize = 11.sp)
        }
    }
}

@Composable
fun BotonAccionClaro(
    modifier: Modifier = Modifier,
    texto: String,
    icono: ImageVector,
    color: Color,
    fondoClaro: Color,
    alto: Int = 80,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(alto.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(fondoClaro)
            .then(Modifier.border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(16.dp)))
            .then(Modifier)
    ) {
        Button(
            onClick   = onClick,
            modifier  = Modifier.fillMaxSize(),
            shape     = RoundedCornerShape(16.dp),
            colors    = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(Modifier.height(4.dp))
                Text(texto, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Compatibilidad con código existente
@Composable
fun TarjetaMetrica(icono: ImageVector, color: Color, titulo: String, valor: String) {}
@Composable
fun BotonAccion(modifier: Modifier = Modifier, texto: String, icono: ImageVector, color: Color, onClick: () -> Unit) {}
