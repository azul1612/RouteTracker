package com.example.routetracker.presentation.estaciones

import java.text.Normalizer

fun urlImagenEstacion(nombre: String): String {
    val normalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "") // quita acentos
        .lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "") // quita signos raros
        .trim()
        .replace(Regex("\\s+"), "-") // espacios -> guiones

    return "https://mexicometro.org/wp-content/uploads/mb-$normalizado-300x300.png"
}