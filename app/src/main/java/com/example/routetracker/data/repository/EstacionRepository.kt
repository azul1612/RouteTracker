package com.example.routetracker.data.repository

import com.example.routetracker.domain.model.Estacion
import com.example.routetracker.domain.model.Linea
import kotlinx.coroutines.flow.Flow

interface EstacionRepository {
    fun getLineas(): Flow<List<Linea>>
    fun getEstacionesPorLinea(lineaId: String): Flow<List<Estacion>>
}