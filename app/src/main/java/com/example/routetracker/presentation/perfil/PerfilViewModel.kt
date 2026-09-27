package com.example.routetracker.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routetracker.data.repository.UsuarioRepository
import com.example.routetracker.data.repository.UsuarioRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: UsuarioRepository = UsuarioRepositoryImpl()
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid ?: ""
    private val email = auth.currentUser?.email ?: ""

    // Valor por defecto: correo sin el "@dominio.com"
    private val nombrePorDefecto = email.substringBefore("@")

    private val _nombreUsuario = MutableStateFlow(nombrePorDefecto)
    val nombreUsuario: StateFlow<String> = _nombreUsuario.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    init {
        if (uid.isNotBlank()) {
            viewModelScope.launch {
                repository.getUsuario(uid).collect { usuario ->
                    // Si ya tiene un nombre guardado, úsalo; si no, mantenemos el default (correo sin @...)
                    if (usuario.nombreUsuario.isNotBlank()) {
                        _nombreUsuario.value = usuario.nombreUsuario
                    }
                }
            }
        }
    }

    fun actualizarNombre(nuevoNombre: String) {
        if (nuevoNombre.isBlank() || uid.isBlank()) return
        _nombreUsuario.value = nuevoNombre
        viewModelScope.launch {
            _guardando.value = true
            repository.actualizarNombreUsuario(uid, nuevoNombre)
            _guardando.value = false
        }
    }
}