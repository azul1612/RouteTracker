package com.example.routetracker.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.routetracker.domain.model.EstadoEstacion
import com.example.routetracker.domain.model.TipoVoto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class VotoRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : VotoRepository {

    override fun getEstadoActual(estacionId: String): Flow<EstadoEstacion> = callbackFlow {
        val listener = firestore.collection("estadoActual").document(estacionId)
            .addSnapshotListener { doc, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val estado = EstadoEstacion(
                    estadoDominante = doc?.getString("estadoDominante") ?: "normal",
                    conteoRetraso = (doc?.getLong("conteoRetraso") ?: 0).toInt(),
                    conteoDetenido = (doc?.getLong("conteoDetenido") ?: 0).toInt(),
                    conteoCerrada = (doc?.getLong("conteoCerrada") ?: 0).toInt()
                )
                trySend(estado)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun registrarVoto(estacionId: String, userId: String, tipo: TipoVoto) {
        // 1. Guardamos el voto individual (para historial/auditoría)
        val votoDatos = hashMapOf(
            "estacionId" to estacionId,
            "userId" to userId,
            "tipo" to tipo.name,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("votos").add(votoDatos).await()

        // 2. Actualizamos el contador correspondiente en estadoActual (atómico)
        val campo = when (tipo) {
            TipoVoto.RETRASO -> "conteoRetraso"
            TipoVoto.DETENIDO -> "conteoDetenido"
            TipoVoto.ESTACION_CERRADA -> "conteoCerrada"
        }
        val estadoDominante = when (tipo) {
            TipoVoto.RETRASO -> "retraso"
            TipoVoto.DETENIDO -> "detenido"
            TipoVoto.ESTACION_CERRADA -> "estacion_cerrada"
        }

        firestore.collection("estadoActual").document(estacionId)
            .set(
                mapOf(
                    campo to FieldValue.increment(1),
                    "estadoDominante" to estadoDominante,
                    "ultimaActualizacion" to FieldValue.serverTimestamp()
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )
            .await()
    }
}