package com.example.routetracker.domain.model

data class EstadoEstacion(
    val estadoDominante: String = "normal",
    val conteoRetraso: Int = 0,
    val conteoDetenido: Int = 0,
    val conteoCerrada: Int = 0
)