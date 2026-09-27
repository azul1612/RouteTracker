package com.example.routetracker.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.routetracker.domain.model.Comentario
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ComentarioRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ComentarioRepository {

    override fun getComentariosPorEstacion(estacionId: String): Flow<List<Comentario>> = callbackFlow {
        val listener = firestore.collection("comentarios")
            .whereEqualTo("estacionId", estacionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val comentarios = snapshot?.documents?.map { doc ->
                    Comentario(
                        id = doc.id,
                        estacionId = doc.getString("estacionId") ?: "",
                        userId = doc.getString("userId") ?: "",
                        nombreUsuario = doc.getString("nombreUsuario") ?: "",
                        texto = doc.getString("texto") ?: "",
                        likes = (doc.getLong("likes") ?: 0).toInt(),
                        dislikes = (doc.getLong("dislikes") ?: 0).toInt()
                    )
                } ?: emptyList()
                trySend(comentarios)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun agregarComentario(comentario: Comentario) {
        val datos = hashMapOf(
            "estacionId" to comentario.estacionId,
            "userId" to comentario.userId,
            "nombreUsuario" to comentario.nombreUsuario,
            "texto" to comentario.texto,
            "likes" to 0,
            "dislikes" to 0,
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        firestore.collection("comentarios").add(datos).await()
    }

    override suspend fun darLike(comentarioId: String) {
        firestore.collection("comentarios").document(comentarioId)
            .update("likes", com.google.firebase.firestore.FieldValue.increment(1))
            .await()
    }
}