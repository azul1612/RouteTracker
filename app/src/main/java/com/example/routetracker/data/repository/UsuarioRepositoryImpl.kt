package com.example.routetracker.data.repository

import com.example.routetracker.domain.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UsuarioRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UsuarioRepository {

    override fun getUsuario(uid: String): Flow<Usuario> = callbackFlow {
        val listener = firestore.collection("usuarios").document(uid)
            .addSnapshotListener { doc, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val usuario = Usuario(
                    uid = uid,
                    email = doc?.getString("email") ?: "",
                    nombreUsuario = doc?.getString("nombreUsuario") ?: ""
                )
                trySend(usuario)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun actualizarNombreUsuario(uid: String, nuevoNombre: String) {
        firestore.collection("usuarios").document(uid)
            .set(mapOf("nombreUsuario" to nuevoNombre), SetOptions.merge())
            .await()
    }
}