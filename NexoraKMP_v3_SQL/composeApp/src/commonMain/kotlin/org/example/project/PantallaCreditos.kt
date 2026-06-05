package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class PantallaCreditos : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize().background(NexoraFondo).verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Box(modifier = Modifier.fillMaxWidth().shadow(4.dp).background(NexoraSuperficie).padding(horizontal = 4.dp, vertical = 12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = NexoraTextoPrimario)
                    }
                    Text("Acerca de", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            // Hero — fondo azul de pantalla (no card de datos)
            Box(
                modifier = Modifier.fillMaxWidth().background(NexoraAzul).padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(72.dp).shadow(8.dp, RoundedCornerShape(20.dp)).clip(RoundedCornerShape(20.dp)).background(NexoraSuperficie),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("N", color = NexoraAzul, fontWeight = FontWeight.ExtraBold, fontSize = 38.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Nexora POS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
                    Text("Versión 1.0.0", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text("Kotlin Multiplatform · Compose", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            SeccionCardClara("Descripción") {
                Text(
                    "Nexora POS es un sistema de punto de venta multiplataforma desarrollado con Kotlin Multiplatform (KMP) y Compose Multiplatform. Permite gestionar productos, registrar ventas con carrito de compras, calcular IVA y generar un historial de transacciones, todo desde una única base de código compartida.",
                    color = NexoraTextoSecundario, fontSize = 13.sp, lineHeight = 21.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            SeccionCardClara("Características principales") {
                FilaCaracteristicaClara(Icons.Default.PointOfSale, NexoraAzul,    NexoraAzulClaro,    "Punto de venta con carrito y múltiples métodos de pago")
                FilaCaracteristicaClara(Icons.Default.Inventory,   NexoraAmarillo, NexoraAmarClaro,  "Gestión de inventario con control de stock")
                FilaCaracteristicaClara(Icons.Default.Star,        NexoraMorado,  NexoraMoradoClaro,  "Historial de ventas en tiempo real")
                FilaCaracteristicaClara(Icons.Default.Devices,     NexoraTeal,    NexoraTealClaro,    "Disponible en Android, iOS y Desktop")
                FilaCaracteristicaClara(Icons.Default.Code,        NexoraVerde,   NexoraVerdeClaro,   "UI 100% compartida con Compose Multiplatform")
            }

            Spacer(Modifier.height(12.dp))

            SeccionCardClara("Stack tecnológico") {
                val techs = listOf(
                    Pair("Kotlin Multiplatform (KMP)", NexoraAzul),
                    Pair("Compose Multiplatform",       NexoraMorado),
                    Pair("Voyager Navigator",            NexoraTeal),
                    Pair("Multiplatform Settings",       NexoraAmarillo),
                    Pair("Kotlinx Serialization",        NexoraVerde),
                    Pair("AndroidX ViewModel",           NexoraAzul),
                    Pair("Kotlinx DateTime",             NexoraTextoSecundario)
                )
                techs.forEach { (tec, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                        Spacer(Modifier.width(10.dp))
                        Text(tec, color = NexoraTextoSecundario, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            SeccionCardClara("Equipo de desarrollo") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(NexoraAzulClaro),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("D", color = NexoraAzul, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {

                        Text("Diego Fernando Tulcán Silva", color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Luis Andres Muñoz León", color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Desarrollador Full Stack KMP", color = NexoraAzul, fontSize = 12.sp)
                        Text("Cod. 2024XXXX", color = NexoraTextoSecundario, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp))
                    .background(NexoraTealClaro).padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(NexoraTeal.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.School, contentDescription = null, tint = NexoraTeal, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Universidad del Cauca", color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Desarrollo de Aplicaciones Móviles", color = NexoraTextoSecundario, fontSize = 12.sp)
                        Text("Prof. Ph.D. Cristhian Figueroa", color = NexoraTextoSecundario, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Text("© 2025 Nexora POS · Todos los derechos reservados", color = NexoraTextoBajo, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SeccionCardClara(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp))
            .background(NexoraSuperficie).padding(16.dp)
    ) {
        Column {
            Text(titulo, color = NexoraTextoPrimario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun FilaCaracteristicaClara(icono: ImageVector, color: Color, fondo: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
        Box(Modifier.size(32.dp).clip(RoundedCornerShape(9.dp)).background(fondo), contentAlignment = Alignment.Center) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(texto, color = NexoraTextoSecundario, fontSize = 13.sp, modifier = Modifier.weight(1f), lineHeight = 18.sp)
    }
}

// Compatibilidad con nombres anteriores
@Composable fun FilaCaracteristica(icono: ImageVector, color: Color, texto: String) = FilaCaracteristicaClara(icono, color, color.copy(alpha = 0.12f), texto)
@Composable fun TarjetaIntegrante(nombre: String, rol: String, codigo: String) {}
