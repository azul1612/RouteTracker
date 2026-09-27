package com.example.routetracker.data.repository

import com.example.routetracker.domain.model.Unidad
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MockUnidadesRepositoryImpl : UnidadesRepository {

    override fun getUnidadesPorEstacion(
        estacionId: String,
        direcciones: List<String>
    ): Flow<List<Unidad>> = flow {
        delay(500) // simula el tiempo real de espera de una llamada de red

        val unidadesFalsas = mutableListOf<Unidad>()
        direcciones.forEachIndexed { index, direccion ->
            repeat(3) { i ->
                unidadesFalsas.add(
                    Unidad(
                        id = "MB-${1000 + index * 100 + i}",
                        nombre = direccion,
                        direccion = direccion,
                        tiempoLlegadaMinutos = Random.nextInt(2, 15)
                    )
                )
            }
        }
        emit(unidadesFalsas)
    }
}