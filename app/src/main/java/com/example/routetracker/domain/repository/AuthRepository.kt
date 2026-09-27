package com.example.routetracker.domain.repository

import com.example.routetracker.domain.model.Usuario

interface AuthRepository {
    suspend fun iniciarSesion(email: String, password: String): Result<Usuario>
    suspend fun registrar(email: String, password: String): Result<Usuario>
    fun cerrarSesion()
    fun usuarioActual(): Usuario?
}