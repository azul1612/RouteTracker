package com.example.routetracker.data.repository

import com.example.routetracker.domain.model.Unidad
import kotlinx.coroutines.flow.Flow

interface UnidadesRepository {
    fun getUnidadesPorEstacion(estacionId: String, direcciones: List<String>): Flow<List<Unidad>>
}