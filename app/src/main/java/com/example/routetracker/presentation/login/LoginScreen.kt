package com.example.routetracker.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var modoRegistro by remember { mutableStateOf(false) } // false = login, true = crear cuenta
    var mostrarDialogoRecuperar by remember { mutableStateOf(false) }
    var mensajeRecuperacion by remember { mutableStateOf<String?>(null) }

    // en cuanto el login/registro sea exitoso, navegamos afuera de esta pantalla
    LaunchedEffect(uiState.loginExitoso) {
        if (uiState.loginExitoso) {
            onLoginExitoso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "RouteTracker",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC1121F)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (modoRegistro) "Crea tu cuenta" else "Inicia sesión para comentar y votar",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; viewModel.limpiarError() },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; viewModel.limpiarError() },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        // mensaje de error, si lo hay
        uiState.error?.let { mensaje ->
            Spacer(Modifier.height(8.dp))
            Text(text = mensaje, color = Color(0xFFB71C1C), fontSize = 13.sp)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (modoRegistro) viewModel.registrar(email, password)
                else viewModel.iniciarSesion(email, password)
            },
            enabled = !uiState.cargando,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC1121F))
        ) {
            if (uiState.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text(if (modoRegistro) "Crear cuenta" else "Iniciar sesión")
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = {
            modoRegistro = !modoRegistro
            viewModel.limpiarError()
        }) {
            Text(
                if (modoRegistro) "¿Ya tienes cuenta? Inicia sesión"
                else "¿No tienes cuenta? Regístrate"
            )
        }
        TextButton(onClick = { mostrarDialogoRecuperar = true }) {
            Text("¿Olvidaste tu contraseña?", fontSize = 13.sp, color = Color.Gray)
        }
    }
    if (mostrarDialogoRecuperar) {
        var correoRecuperar by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { mostrarDialogoRecuperar = false },
            title = { Text("Recuperar contraseña") },
            text = {
                Column {
                    Text("Ingresa tu correo y te enviaremos un enlace para restablecerla.", fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = correoRecuperar,
                        onValueChange = { correoRecuperar = it },
                        placeholder = { Text("ejemplo@correo.com") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    mensajeRecuperacion?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, fontSize = 12.sp, color = Color(0xFFC1121F))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(correoRecuperar)
                        .addOnSuccessListener {
                            mensajeRecuperacion = "Correo enviado. Revisa tu bandeja de entrada."
                        }
                        .addOnFailureListener {
                            mensajeRecuperacion = "No se pudo enviar. Verifica el correo."
                        }
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoRecuperar = false
                    mensajeRecuperacion = null
                }) { Text("Cancelar") }
            }
        )
    }
}
