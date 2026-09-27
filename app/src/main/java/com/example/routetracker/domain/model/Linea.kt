package com.example.routetracker.domain.model

data class Linea(
    val id: String = "",
    val nombre: String = "",
    val colorHex: String = "",
    val direcciones: List<String> = emptyList()
)