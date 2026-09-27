package com.example.routetracker.presentation.detalleestacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routetracker.domain.model.Comentario
import com.example.routetracker.domain.model.TipoVoto
import com.example.routetracker.domain.model.Unidad
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import com.example.routetracker.presentation.common.MenuSuperior
import com.example.routetracker.presentation.common.SelectorLineas
import androidx.compose.ui.graphics.Color

@Composable
fun DetalleEstacionScreen(
    estacionId: String,
    lineaId: String,
    nombreEstacion: String,
    direcciones: List<String>,
    onPerfil: () -> Unit = {},
    onIrInicio: () -> Unit = {},
    onVolver: () -> Unit = {},
    onCambiarLinea: (String) -> Unit = {},
    onMapa: (() -> Unit)? = null
) {
    val viewModel: DetalleEstacionViewModel = viewModel(
        key = estacionId,
        factory = DetalleEstacionViewModelFactory(estacionId, direcciones)
    )

    val unidades by viewModel.unidades.collectAsState()
    val comentarios by viewModel.comentarios.collectAsState()
    val estado by viewModel.estado.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var mostrarDialogoVoto by remember { mutableStateOf<TipoVoto?>(null) }

    if (cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        MenuSuperior(onPerfil = onPerfil, onInicio = onIrInicio, onAtras = onVolver, onMapa = onMapa)
        SelectorLineas(lineaActivaId = lineaId, onCambiarLinea = onCambiarLinea)

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(text = nombreEstacion, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            // --- TABLITAS DE UNIDADES, una por dirección ---
            items(direcciones) { direccion ->
                TablaUnidades(
                    direccion = direccion,
                    unidades = unidades.filter { it.direccion == direccion }
                )
            }

            // --- SECCIÓN COMENTARIOS ---
            item {
                SeccionComentarios(comentarios = comentarios, onLike = { id -> viewModel.darLike(id) })
            }

            // --- VOTACIÓN SEMÁFORO ---
            item {
                VotacionSemaforo(
                    conteoRetraso = estado.conteoRetraso,
                    conteoDetenido = estado.conteoDetenido,
                    conteoCerrada = estado.conteoCerrada,
                    onVotar = { tipo -> mostrarDialogoVoto = tipo }
                )
            }
        }
    }

    // --- POP-UP al votar, para dejar comentario opcional ---
    mostrarDialogoVoto?.let { tipo ->
        DialogoComentarioVoto(
            onConfirmar = { texto ->
                viewModel.votar(tipo)
                if (texto.isNotBlank()) viewModel.enviarComentario(texto)
                mostrarDialogoVoto = null
            },
            onCancelar = { mostrarDialogoVoto = null }
        )
    }
}

@Composable
fun TablaUnidades(direccion: String, unidades: List<Unidad>) {
    Column {
        Text(text = "Dirección: $direccion", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    Text("UNIDAD", Modifier.weight(1f), fontSize = 11.sp, color = androidx.compose.ui.graphics.Color.Gray)
                    Text("NOMBRE", Modifier.weight(2f), fontSize = 11.sp, color = androidx.compose.ui.graphics.Color.Gray)
                    Text("TIEMPO", fontSize = 11.sp, color = androidx.compose.ui.graphics.Color.Gray)
                }
                Spacer(Modifier.height(6.dp))
                unidades.forEach { unidad ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(unidad.id, Modifier.weight(1f), fontSize = 13.sp)
                        Text(unidad.nombre, Modifier.weight(2f), fontSize = 13.sp)
                        Text("${unidad.tiempoLlegadaMinutos} min", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionComentarios(comentarios: List<Comentario>, onLike: (String) -> Unit) {

    Column {
        Text(text = "Comentarios de usuarios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(12.dp).heightIn(max = 260.dp)) {
                if (comentarios.isEmpty()) {
                    Text("Aún no hay comentarios en esta estación.", fontSize = 13.sp, color = Color.Gray)
                } else {
                    comentarios.forEach { comentario ->
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Text(comentario.nombreUsuario, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(comentario.texto, fontSize = 13.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onLike(comentario.id) }
                            ) {
                                Icon(Icons.Default.ThumbUp, contentDescription = "Me gusta", modifier = Modifier.size(14.dp))
                                Text(" ${comentario.likes}  ", fontSize = 12.sp)
                                Icon(Icons.Default.ThumbDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text(" ${comentario.dislikes}", fontSize = 12.sp)
                            }
                            HorizontalDivider(Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun VotacionSemaforo(
    conteoRetraso: Int,
    conteoDetenido: Int,
    conteoCerrada: Int,
    onVotar: (TipoVoto) -> Unit
) {
    Column {
        Text(text = "¿Cómo va el servicio?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            OpcionVoto("Retraso", conteoRetraso, androidx.compose.ui.graphics.Color(0xFFFFC107)) { onVotar(TipoVoto.RETRASO) }
            OpcionVoto("Detenido", conteoDetenido, androidx.compose.ui.graphics.Color(0xFFF44336)) { onVotar(TipoVoto.DETENIDO) }
            OpcionVoto("Est. cerrada", conteoCerrada, androidx.compose.ui.graphics.Color(0xFFB71C1C)) { onVotar(TipoVoto.ESTACION_CERRADA) }
        }
    }
}

@Composable
fun OpcionVoto(etiqueta: String, conteo: Int, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickableSinRipple(onClick)) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color, shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(conteo.toString(), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(etiqueta, fontSize = 12.sp)
    }
}

// pequeño helper para hacer clickeable sin efecto visual de "onda" (ripple), más limpio para este botón circular
@Composable
fun Modifier.clickableSinRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = interactionSource,
            onClick = onClick
        )
    )
}
@Composable
fun DialogoComentarioVoto(onConfirmar: (String) -> Unit, onCancelar: () -> Unit) {
    var texto by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("¿Quieres dejar algún comentario?") },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                placeholder = { Text("Opcional") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(texto) }) { Text("Enviar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
