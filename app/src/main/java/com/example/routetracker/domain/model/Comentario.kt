package com.example.routetracker.domain.model

data class Comentario(
    val id: String = "",
    val estacionId: String = "",
    val userId: String = "",
    val nombreUsuario: String = "",
    val texto: String = "",
    val likes: Int = 0,
    val dislikes: Int = 0
)