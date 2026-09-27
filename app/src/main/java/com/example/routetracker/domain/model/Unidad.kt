package com.example.routetracker.domain.model

data class Unidad(
    val id: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val tiempoLlegadaMinutos: Int = 0
)