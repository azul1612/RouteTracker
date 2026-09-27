package com.example.routetracker.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routetracker.domain.repository.AuthRepository
import com.example.routetracker.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val loginExitoso: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun iniciarSesion(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa correo y contraseña")
            return
        }
        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        viewModelScope.launch {
            val resultado = authRepository.iniciarSesion(email, password)
            resultado.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(cargando = false, loginExitoso = true)
                },
                onFailure = { excepcion ->
                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        error = mapearError(excepcion.message)
                    )
                }
            )
        }
    }

    fun registrar(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa correo y contraseña")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }
        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        viewModelScope.launch {
            val resultado = authRepository.registrar(email, password)
            resultado.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(cargando = false, loginExitoso = true)
                },
                onFailure = { excepcion ->
                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        error = mapearError(excepcion.message)
                    )
                }
            )
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // traduce los mensajes técnicos de Firebase a algo entendible para el usuario
    private fun mapearError(mensajeOriginal: String?): String {
        return when {
            mensajeOriginal?.contains("badly formatted") == true -> "El correo no es válido"
            mensajeOriginal?.contains("no user record") == true -> "No existe una cuenta con ese correo"
            mensajeOriginal?.contains("password is invalid") == true -> "Contraseña incorrecta"
            mensajeOriginal?.contains("already in use") == true -> "Ya existe una cuenta con ese correo"
            mensajeOriginal?.contains("network") == true -> "Sin conexión a internet"
            else -> "Ocurrió un error, intenta de nuevo"
        }
    }
}