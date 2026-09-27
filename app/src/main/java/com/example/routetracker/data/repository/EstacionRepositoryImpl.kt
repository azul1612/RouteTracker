package com.example.routetracker.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.routetracker.domain.model.Estacion
import com.example.routetracker.domain.model.Linea
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EstacionRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : EstacionRepository {

    override fun getLineas(): Flow<List<Linea>> = callbackFlow {
        val listener = firestore.collection("lineas")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lineas = snapshot?.documents?.map { doc ->
                    Linea(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        colorHex = doc.getString("colorHex") ?: "",
                        direcciones = doc.get("direcciones") as? List<String> ?: emptyList()
                    )
                } ?: emptyList()
                trySend(lineas)
            }
        awaitClose { listener.remove() }
    }

    override fun getEstacionesPorLinea(lineaId: String): Flow<List<Estacion>> = callbackFlow {
        val listener = firestore.collection("estaciones")
            .whereEqualTo("lineaId", lineaId)
            .orderBy("orden")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val estaciones = snapshot?.documents?.map { doc ->
                    Estacion(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        lineaId = doc.getString("lineaId") ?: "",
                        orden = (doc.getLong("orden") ?: 0).toInt(),
                        lat = doc.getDouble("lat") ?: 0.0,
                        lng = doc.getDouble("lng") ?: 0.0,
                        gtfsStopId = doc.getString("gtfsStopId") ?: ""
                    )
                } ?: emptyList()
                trySend(estaciones)
            }
        awaitClose { listener.remove() }
    }
}