package com.example.routetracker.domain.model

enum class TipoVoto {
    RETRASO, DETENIDO, ESTACION_CERRADA
}

data class Voto(
    val id: String = "",
    val estacionId: String = "",
    val userId: String = "",
    val tipo: TipoVoto = TipoVoto.RETRASO
)