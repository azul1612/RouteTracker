package com.example.routetracker.data.repository

import com.example.routetracker.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun getUsuario(uid: String): Flow<Usuario>
    suspend fun actualizarNombreUsuario(uid: String, nuevoNombre: String)
}