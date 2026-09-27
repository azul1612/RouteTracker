package com.example.routetracker.presentation.detalleestacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetalleEstacionViewModelFactory(
    private val estacionId: String,
    private val direcciones: List<String>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DetalleEstacionViewModel(estacionId, direcciones) as T
    }
}