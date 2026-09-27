package com.example.routetracker.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routetracker.R
import com.example.routetracker.domain.model.Linea
import coil3.compose.SubcomposeAsyncImage
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.routetracker.presentation.common.coloresLinea

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onLineaClick: (String) -> Unit = {},
    onMapaClick: () -> Unit = {}
) {
    val lineas by viewModel.lineas.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // --- HEADER ---
        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            Image(
                painter = painterResource(id = R.drawable.header_metrobus),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))
                )
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text("🚌 Route Tracker CDMX", color = Color.White, fontSize = 12.sp)
                Text(
                    "Seguimiento de Unidades de Metrobús",
                    color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }
        }

        // --- ACCESO AL MAPA DE ESTACIONES ---
        FilledTonalButton(
            onClick = onMapaClick,
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp)
        ) {
            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Ver mapa de estaciones")
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SELECCIONA UNA LÍNEA", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(Color(0xFF2E7D32)))
                Spacer(Modifier.width(4.dp))
                Text("En tiempo real", fontSize = 12.sp, color = Color.Gray)
            }
        }

        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lineas) { linea ->
                    LineaCard(linea = linea, onClick = { onLineaClick(linea.id) })
                }
            }
        }
    }
}

val urlImagenLinea = mapOf(
    "linea1" to "https://thumb.wikimedia.org/wikipedia/commons/thumb/9/91/Mexico_City_Metrob%C3%BAs_Line_1_icon.svg/960px-Mexico_City_Metrob%C3%BAs_Line_1_icon.svg.png?utm_source=es.wikipedia.org&utm_campaign=index&utm_content=thumbnail",
    "linea2" to "https://thumb.wikimedia.org/wikipedia/commons/thumb/9/91/Mexico_City_Metrob%C3%BAs_Line_2_icon.svg/960px-Mexico_City_Metrob%C3%BAs_Line_2_icon.svg.png?utm_source=es.wikipedia.org&utm_campaign=index&utm_content=thumbnail",
    "linea3" to "https://thumb.wikimedia.org/wikipedia/commons/thumb/3/3f/Mexico_City_Metrob%C3%BAs_Line_3_icon.svg/3840px-Mexico_City_Metrob%C3%BAs_Line_3_icon.svg.png?utm_source=en.wikipedia.org&utm_campaign=index&utm_content=thumbnail",
    "linea4" to "https://thumb.wikimedia.org/wikipedia/commons/thumb/b/b1/Mexico_City_Metrob%C3%BAs_Line_4_icon.svg/960px-Mexico_City_Metrob%C3%BAs_Line_4_icon.svg.png?utm_source=es.wikipedia.org&utm_campaign=index&utm_content=thumbnail",
    "linea5" to "https://thumb.wikimedia.org/wikipedia/commons/thumb/c/c5/Mexico_City_Metrob%C3%BAs_Line_5_icon.svg/960px-Mexico_City_Metrob%C3%BAs_Line_5_icon.svg.png?utm_source=es.wikipedia.org&utm_campaign=index&utm_content=thumbnail",
    "linea6" to "https://thumb.wikimedia.org/wikipedia/commons/thumb/1/1d/Mexico_City_Metrob%C3%BAs_Line_6_icon.svg/960px-Mexico_City_Metrob%C3%BAs_Line_6_icon.svg.png?utm_source=es.wikipedia.org&utm_campaign=index&utm_content=thumbnail",
    "linea7" to "https://thumb.wikimedia.org/wikipedia/commons/thumb/d/d2/Mexico_City_Metrob%C3%BAs_Line_7_icon.svg/3840px-Mexico_City_Metrob%C3%BAs_Line_7_icon.svg.png?utm_source=es.wikipedia.org&utm_campaign=index&utm_content=thumbnail"
)

@Composable
fun LineaCard(linea: Linea, onClick: () -> Unit) {
    val color = coloresLinea[linea.id] ?: Color.Gray
    val descripcion = linea.direcciones.joinToString(" – ")

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
                    model = urlImagenLinea[linea.id],
                    contentDescription = linea.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize().background(color.copy(alpha = 0.3f)))
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(linea.id.removePrefix("linea"), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(color)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(linea.nombre, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(descripcion, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}