package com.example.routetracker.presentation.estaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routetracker.domain.model.Estacion
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import androidx.compose.material.icons.filled.Tram
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import com.example.routetracker.presentation.common.*


@Composable
fun EstacionesScreen(
    lineaId: String,
    onEstacionClick: (String) -> Unit = {},
    onCambiarLinea: (String) -> Unit = {},
    onVolver: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onIrInicio: () -> Unit = {},
    onMapa: (() -> Unit)? = null
) {
    val viewModel: EstacionesViewModel = viewModel(
        key = lineaId, // importante: fuerza un ViewModel nuevo si cambia de línea
        factory = EstacionesViewModelFactory(lineaId)
    )
    val estaciones by viewModel.estaciones.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val numeroLinea = lineaId.removePrefix("linea")

    Column(modifier = Modifier.fillMaxSize()) {

        // Reemplaza el menú viejo por este:
        MenuSuperior(
            onPerfil = onPerfil,
            onInicio = onIrInicio,
            onAtras = onVolver,
            onMapa = onMapa
        )

        // Reemplaza los círculos viejos por este:
        SelectorLineas(
            lineaActivaId = lineaId,
            onCambiarLinea = onCambiarLinea
        )

        Spacer(Modifier.height(16.dp))

        // --- TÍTULO ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(4.dp).height(22.dp).background(coloresLinea[lineaId] ?: Color.Gray))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Estaciones Línea $numeroLinea",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        // --- LISTA DE ESTACIONES ---
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estaciones) { estacion ->
                    EstacionItem(estacion = estacion, onClick = { onEstacionClick(estacion.id) })
                }
            }
        }
    }
}

@Composable
fun EstacionItem(estacion: Estacion, onClick: () -> Unit) {
    val colorLinea = coloresLinea[estacion.lineaId] ?: Color.Gray

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubcomposeAsyncImage(
                    model = urlImagenEstacion(estacion.nombre),
                    contentDescription = estacion.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorLinea.copy(alpha = 0.3f), CircleShape)
                        )
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorLinea, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Tram,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
                Spacer(Modifier.width(12.dp))
                Text(text = estacion.nombre, fontSize = 15.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}