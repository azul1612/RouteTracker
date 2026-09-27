package com.example.routetracker.presentation.estaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EstacionesViewModelFactory(
    private val lineaId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EstacionesViewModel(lineaId) as T
    }
}
