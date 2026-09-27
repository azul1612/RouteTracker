package com.example.routetracker.data.repository

import com.example.routetracker.domain.model.Comentario
import kotlinx.coroutines.flow.Flow

interface ComentarioRepository {
    fun getComentariosPorEstacion(estacionId: String): Flow<List<Comentario>>
    suspend fun agregarComentario(comentario: Comentario)
    suspend fun darLike(comentarioId: String)
}