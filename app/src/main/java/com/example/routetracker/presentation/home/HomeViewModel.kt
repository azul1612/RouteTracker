package com.example.routetracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routetracker.data.repository.EstacionRepository
import com.example.routetracker.data.repository.EstacionRepositoryImpl
import com.example.routetracker.domain.model.Linea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel @JvmOverloads constructor(
    private val repository: EstacionRepository = EstacionRepositoryImpl()
) : ViewModel() {

    private val _lineas = MutableStateFlow<List<Linea>>(emptyList())
    val lineas: StateFlow<List<Linea>> = _lineas.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        cargarLineas()
    }

    private fun cargarLineas() {
        viewModelScope.launch {
            repository.getLineas().collect { listaLineas ->
                _lineas.value = listaLineas
                _cargando.value = false
            }
        }
    }
}