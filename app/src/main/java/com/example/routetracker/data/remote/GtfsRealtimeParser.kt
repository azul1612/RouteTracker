package com.example.routetracker.data.remote

import com.example.routetracker.domain.model.Unidad
import android.util.Log

data class LlegadaCruda(
    val vehicleId: String?,
    val directionId: Int?,
    val stopId: String,
    val tiempoEpochSegundos: Long
)

object GtfsRealtimeParser {

    fun parseLlegadasParaEstacion(feedBytes: ByteArray, gtfsStopId: String): List<LlegadaCruda> {
        val resultado = mutableListOf<LlegadaCruda>()
        val feedFields = ProtoReader(feedBytes).readAllFields()

        val entidades = feedFields[2].orEmpty()
        Log.d("METROBUS_DEBUG", "Total de entidades en el feed: ${entidades.size}")

        var conTripUpdate = 0
        val stopIdsEjemplo = mutableSetOf<String>()

        for (entidadRaw in entidades) {
            val entidadBytes = entidadRaw as? ByteArray ?: continue
            val entidadFields = ProtoReader(entidadBytes).readAllFields()

            if (entidadFields[3] == null) {
                Log.d("METROBUS_DEBUG", "Campos presentes en esta entidad: ${entidadFields.keys}")
            }


            val tripUpdateBytes = (entidadFields[3]?.firstOrNull() as? ByteArray) ?: continue
            conTripUpdate++
            val tripUpdateFields = ProtoReader(tripUpdateBytes).readAllFields()

            var directionId: Int? = null
            val tripDescriptorBytes = tripUpdateFields[1]?.firstOrNull() as? ByteArray
            if (tripDescriptorBytes != null) {
                val tripFields = ProtoReader(tripDescriptorBytes).readAllFields()
                directionId = (tripFields[6]?.firstOrNull() as? Long)?.toInt()
            }

            var vehicleId: String? = null
            val vehicleDescBytes = tripUpdateFields[3]?.firstOrNull() as? ByteArray
            if (vehicleDescBytes != null) {
                val vehicleFields = ProtoReader(vehicleDescBytes).readAllFields()
                vehicleId = (vehicleFields[1]?.firstOrNull() as? ByteArray)?.asProtoText()
            }

            val stopTimeUpdates = tripUpdateFields[2].orEmpty()
            for (stuRaw in stopTimeUpdates) {
                val stuBytes = stuRaw as? ByteArray ?: continue
                val stuFields = ProtoReader(stuBytes).readAllFields()

                val stopId = (stuFields[4]?.firstOrNull() as? ByteArray)?.asProtoText() ?: continue
                if (stopIdsEjemplo.size < 15) stopIdsEjemplo.add(stopId)

                if (stopId != gtfsStopId) continue

                val eventoBytes = (stuFields[2]?.firstOrNull() as? ByteArray)
                    ?: (stuFields[3]?.firstOrNull() as? ByteArray)
                    ?: continue
                val eventoFields = ProtoReader(eventoBytes).readAllFields()
                val tiempoEpoch = (eventoFields[2]?.firstOrNull() as? Long) ?: continue

                resultado.add(LlegadaCruda(vehicleId, directionId, stopId, tiempoEpoch))
            }
        }

        Log.d("METROBUS_DEBUG", "Entidades con trip_update: $conTripUpdate")
        Log.d("METROBUS_DEBUG", "Ejemplo de stop_id reales en el feed: $stopIdsEjemplo")
        Log.d("METROBUS_DEBUG", "Buscábamos: $gtfsStopId")

        return resultado
    }
}