package com.example.routetracker.domain.model

data class Estacion(
    val id: String = "",
    val nombre: String = "",
    val lineaId: String = "",
    val orden: Int = 0,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val gtfsStopId: String = ""
)