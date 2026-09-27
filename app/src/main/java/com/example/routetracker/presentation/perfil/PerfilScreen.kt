package com.example.routetracker.presentation.perfil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.statusBarsPadding
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel(),
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    val usuarioActual = FirebaseAuth.getInstance().currentUser
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()
    var editando by remember { mutableStateOf(false) }
    var textoTemporal by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                modifier = Modifier.size(24.dp).clickable { onVolver() },
                tint = Color.DarkGray
            )
            Spacer(Modifier.width(12.dp))
            Text("Mi perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = Color(0xFFC1121F)
            )
            Spacer(Modifier.height(16.dp))

            if (editando) {
                OutlinedTextField(
                    value = textoTemporal,
                    onValueChange = { textoTemporal = it },
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        viewModel.actualizarNombre(textoTemporal)
                        editando = false
                    }) { Text("Guardar") }
                    TextButton(onClick = { editando = false }) { Text("Cancelar") }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(nombreUsuario, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar nombre",
                        modifier = Modifier.size(18.dp).clickable {
                            textoTemporal = nombreUsuario
                            editando = true
                        },
                        tint = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = usuarioActual?.email ?: "Sin correo registrado",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onCerrarSesion()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC1121F))
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}