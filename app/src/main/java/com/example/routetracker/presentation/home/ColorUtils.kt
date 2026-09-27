package com.example.routetracker.presentation.home

import androidx.compose.ui.graphics.Color

fun hexToColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}