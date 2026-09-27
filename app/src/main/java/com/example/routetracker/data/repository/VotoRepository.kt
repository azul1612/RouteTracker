package com.example.routetracker.data.repository

import com.example.routetracker.domain.model.EstadoEstacion
import com.example.routetracker.domain.model.TipoVoto
import kotlinx.coroutines.flow.Flow

interface VotoRepository {
    fun getEstadoActual(estacionId: String): Flow<EstadoEstacion>
    suspend fun registrarVoto(estacionId: String, userId: String, tipo: TipoVoto)
}