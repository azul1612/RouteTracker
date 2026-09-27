package com.example.routetracker.presentation.detalleestacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routetracker.data.repository.*
import com.example.routetracker.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth


class DetalleEstacionViewModel(
    private val estacionId: String,
    private val direcciones: List<String>,
    private val unidadesRepository: UnidadesRepository = MockUnidadesRepositoryImpl(),
    private val comentarioRepository: ComentarioRepository = ComentarioRepositoryImpl(),
    private val votoRepository: VotoRepository = VotoRepositoryImpl()
) : ViewModel() {

    private val _unidades = MutableStateFlow<List<Unidad>>(emptyList())
    val unidades: StateFlow<List<Unidad>> = _unidades.asStateFlow()

    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    val comentarios: StateFlow<List<Comentario>> = _comentarios.asStateFlow()

    private val _estado = MutableStateFlow(EstadoEstacion())
    val estado: StateFlow<EstadoEstacion> = _estado.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        viewModelScope.launch {
            unidadesRepository.getUnidadesPorEstacion(estacionId, direcciones).collect {
                _unidades.value = it
                _cargando.value = false
            }
        }
        viewModelScope.launch {
            comentarioRepository.getComentariosPorEstacion(estacionId).collect {
                _comentarios.value = it
            }
        }
        viewModelScope.launch {
            votoRepository.getEstadoActual(estacionId).collect {
                _estado.value = it
            }
        }
    }

    fun enviarComentario(texto: String) {
        if (texto.isBlank()) return
        val usuario = FirebaseAuth.getInstance().currentUser
        viewModelScope.launch {
            comentarioRepository.agregarComentario(
                Comentario(
                    estacionId = estacionId,
                    userId = usuario?.uid ?: "anonimo",
                    nombreUsuario = usuario?.email ?: "Usuario anónimo",
                    texto = texto
                )
            )
        }
    }

    fun votar(tipo: TipoVoto) {
        val usuario = FirebaseAuth.getInstance().currentUser
        viewModelScope.launch {
            votoRepository.registrarVoto(estacionId, usuario?.uid ?: "anonimo", tipo)
        }
    }

    fun darLike(comentarioId: String) {
        viewModelScope.launch {
            comentarioRepository.darLike(comentarioId)
        }
    }
}

