package com.example.routetracker.data.repository

import com.example.routetracker.domain.model.Usuario
import com.example.routetracker.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(email: String, password: String): Result<Usuario> {
        return try {
            val resultado = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val usuarioFirebase = resultado.user
            if (usuarioFirebase != null) {
                Result.success(Usuario(uid = usuarioFirebase.uid, email = usuarioFirebase.email ?: ""))
            } else {
                Result.failure(Exception("No se pudo obtener el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrar(email: String, password: String): Result<Usuario> {
        return try {
            val resultado = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val usuarioFirebase = resultado.user
            if (usuarioFirebase != null) {
                Result.success(Usuario(uid = usuarioFirebase.uid, email = usuarioFirebase.email ?: ""))
            } else {
                Result.failure(Exception("No se pudo crear el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    override fun usuarioActual(): Usuario? {
        val usuarioFirebase = firebaseAuth.currentUser
        return usuarioFirebase?.let { Usuario(uid = it.uid, email = it.email ?: "") }
    }
}