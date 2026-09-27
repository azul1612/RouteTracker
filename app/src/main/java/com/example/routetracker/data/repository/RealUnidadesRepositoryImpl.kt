package com.example.routetracker.data.repository

import com.example.routetracker.data.remote.GtfsRealtimeParser
import com.example.routetracker.domain.model.Unidad
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import android.util.Log

class RealUnidadesRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val client: OkHttpClient = OkHttpClient()
) : UnidadesRepository {

    override fun getUnidadesPorEstacion(
        estacionId: String,
        direcciones: List<String>
    ): Flow<List<Unidad>> = flow {
        val configDoc = firestore.collection("config").document("gtfsRealtime").get().await()
        val url = configDoc.getString("urlTripUpdates")
        if (url.isNullOrBlank()) { emit(emptyList()); return@flow }

        val estacionDoc = firestore.collection("estaciones").document(estacionId).get().await()
        val gtfsStopId = estacionDoc.getString("gtfsStopId")
        Log.d("METROBUS_DEBUG", "URL leída de Firestore: $url")
        Log.d("METROBUS_DEBUG", "gtfsStopId de la estación: $gtfsStopId")
        if (gtfsStopId.isNullOrBlank()) { emit(emptyList()); return@flow }

        val unidades = withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                Log.d("METROBUS_DEBUG", "Código de respuesta HTTP: ${response.code}")

                val bytes = response.body?.bytes() ?: return@withContext emptyList()
                Log.d("METROBUS_DEBUG", "Bytes descargados: ${bytes.size}")

                val llegadas = GtfsRealtimeParser.parseLlegadasParaEstacion(bytes, gtfsStopId)
                Log.d("METROBUS_DEBUG", "gtfsStopId buscado: $gtfsStopId")
                Log.d("METROBUS_DEBUG", "Llegadas encontradas (antes de filtrar): ${llegadas.size}")

                val ahoraEpoch = System.currentTimeMillis() / 1000
                llegadas.mapNotNull { llegada ->
                    val minutos = ((llegada.tiempoEpochSegundos - ahoraEpoch) / 60).toInt()
                    Log.d("METROBUS_DEBUG", "Llegada: stopId=${llegada.stopId} minutos=$minutos direction=${llegada.directionId}")
                    if (minutos < 0) return@mapNotNull null
                    val direccionNombre = direcciones.getOrElse(llegada.directionId ?: 0) { "Dirección desconocida" }
                    Unidad(
                        id = llegada.vehicleId ?: "unidad",
                        nombre = direccionNombre,
                        direccion = direccionNombre,
                        tiempoLlegadaMinutos = minutos
                    )
                }
            } catch (e: Exception) {
                Log.e("METROBUS_DEBUG", "Excepción atrapada: ${e.message}", e)
                emptyList()
            }
        }
        emit(unidades)
    }
}