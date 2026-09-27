package com.example.routetracker.presentation.estaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routetracker.data.repository.EstacionRepository
import com.example.routetracker.data.repository.EstacionRepositoryImpl
import com.example.routetracker.domain.model.Estacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EstacionesViewModel(
    private val lineaId: String,
    private val repository: EstacionRepository = EstacionRepositoryImpl()
) : ViewModel() {

    private val _estaciones = MutableStateFlow<List<Estacion>>(emptyList())
    val estaciones: StateFlow<List<Estacion>> = _estaciones.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getEstacionesPorLinea(lineaId).collect { lista ->
                _estaciones.value = lista
                _cargando.value = false
            }
        }
    }
}