package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class PantallaLogin : Screen {
    @Composable
    override fun Content() {
        val navigator    = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current

        var correo             by remember { mutableStateOf("") }
        var contrasena         by remember { mutableStateOf("") }
        var mostrarContrasena  by remember { mutableStateOf(false) }
        var errorCorreo        by remember { mutableStateOf(false) }
        var mensajeErrorCorreo by remember { mutableStateOf("") }
        var errorContrasena    by remember { mutableStateOf(false) }
        var mensajeErrorCont   by remember { mutableStateOf("") }

        val correoDemo     = "admin@nexora.com"
        val contrasenaDemo = "nexora123"

        fun validar(): Boolean {
            var ok = true
            when {
                correo.isBlank()                               -> { errorCorreo = true; mensajeErrorCorreo = "El correo es obligatorio"; ok = false }
                !correo.contains("@") || !correo.contains(".") -> { errorCorreo = true; mensajeErrorCorreo = "Ingresa un correo válido"; ok = false }
                else                                           -> { errorCorreo = false; mensajeErrorCorreo = "" }
            }
            when {
                contrasena.isBlank()    -> { errorContrasena = true; mensajeErrorCont = "La contraseña es obligatoria"; ok = false }
                contrasena.length < 6   -> { errorContrasena = true; mensajeErrorCont = "Mínimo 6 caracteres"; ok = false }
                else                    -> { errorContrasena = false; mensajeErrorCont = "" }
            }
            return ok
        }

        Box(
            modifier = Modifier.fillMaxSize().background(NexoraFondo)
        ) {
            // Decoración superior de color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(NexoraAzul)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(52.dp))

                // Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(8.dp, RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(NexoraSuperficie),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Store,
                        contentDescription = null,
                        tint     = NexoraAzul,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text("Nexora POS", color = NexoraSuperficie, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
                Text("Sistema de Punto de Venta", color = NexoraSuperficie.copy(alpha = 0.8f), fontSize = 13.sp)

                Spacer(Modifier.height(28.dp))

                // Tarjeta formulario
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(NexoraSuperficie)
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Bienvenido", color = NexoraTextoPrimario, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Ingresa tus credenciales", color = NexoraTextoSecundario, fontSize = 13.sp)
                        Spacer(Modifier.height(24.dp))

                        // Campo correo
                        CampoClaro(
                            valor       = correo,
                            onCambio    = { correo = it; errorCorreo = false },
                            etiqueta    = "Correo electrónico",
                            placeholder = "ejemplo@correo.com",
                            icono       = Icons.Default.Email,
                            isError     = errorCorreo,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                        AnimatedVisibility(errorCorreo && mensajeErrorCorreo.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                            Text(mensajeErrorCorreo, color = NexoraRojo, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 3.dp))
                        }

                        Spacer(Modifier.height(16.dp))

                        // Campo contraseña
                        CampoClaro(
                            valor       = contrasena,
                            onCambio    = { contrasena = it; errorContrasena = false },
                            etiqueta    = "Contraseña",
                            placeholder = "••••••••",
                            icono       = Icons.Default.Lock,
                            isError     = errorContrasena,
                            visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                                    Icon(
                                        if (mostrarContrasena) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null, tint = NexoraTextoBajo, modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); if (validar()) navigator.replace(PantallaDashboard()) })
                        )
                        AnimatedVisibility(errorContrasena && mensajeErrorCont.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                            Text(mensajeErrorCont, color = NexoraRojo, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 3.dp))
                        }

                        Spacer(Modifier.height(28.dp))

                        Button(
                            onClick   = { focusManager.clearFocus(); if (validar()) navigator.replace(PantallaDashboard()) },
                            modifier  = Modifier.fillMaxWidth().height(52.dp),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = ButtonDefaults.buttonColors(containerColor = NexoraAzul),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text("Ingresar al sistema", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Chip demo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NexoraAzulClaro)
                        .border(1.dp, NexoraAzul.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Credenciales demo", color = NexoraAzulOsc, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(3.dp))
                        Text("$correoDemo  ·  $contrasenaDemo", color = NexoraTextoSecundario, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CampoClaro(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    placeholder: String = "",
    icono: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value           = valor,
        onValueChange   = onCambio,
        modifier        = Modifier.fillMaxWidth(),
        label           = { Text(etiqueta, fontSize = 12.sp) },
        placeholder     = { Text(placeholder, color = NexoraTextoBajo, fontSize = 13.sp) },
        leadingIcon     = if (icono != null) ({
            Icon(icono, contentDescription = null,
                tint = if (isError) NexoraRojo else if (valor.isNotEmpty()) NexoraAzul else NexoraTextoBajo,
                modifier = Modifier.size(18.dp))
        }) else null,
        trailingIcon    = trailingIcon,
        isError         = isError,
        singleLine      = true,
        shape           = RoundedCornerShape(14.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = if (isError) NexoraRojo else NexoraAzul,
            unfocusedBorderColor    = if (isError) NexoraRojo else NexoraBorde,
            focusedLabelColor       = if (isError) NexoraRojo else NexoraAzul,
            unfocusedLabelColor     = NexoraTextoSecundario,
            focusedTextColor        = NexoraTextoPrimario,
            unfocusedTextColor      = NexoraTextoPrimario,
            cursorColor             = NexoraAzul,
            focusedContainerColor   = NexoraSuperficie,
            unfocusedContainerColor = NexoraFondo,
            errorBorderColor        = NexoraRojo,
            errorLabelColor         = NexoraRojo,
            errorContainerColor     = NexoraRojoClaro,
        )
    )
}
