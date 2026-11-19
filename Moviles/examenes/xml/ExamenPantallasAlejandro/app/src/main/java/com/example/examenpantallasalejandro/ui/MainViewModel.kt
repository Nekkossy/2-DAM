package com.example.examenpantallasalejandro.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.examenpantallasalejandro.domain.useCases.DameInicioUseCase
import com.example.examenpantallasalejandro.domain.useCases.DameTareasUseCase

class MainViewModel (
    private val dameInicioUseCase: DameInicioUseCase,
    private val dameTareasUseCase: DameTareasUseCase,
) : ViewModel() {
    private val _state = MutableLiveData(MainState(
        checked = false,
        inicioText = "inicio",
        tareasText = "tareas",
        estadisticasText = "estadisticas",
        perfilText = "perfil",
        contadorInicio = 0,
        contadorTareas = 0,
        contadorEstadisticas = 0,
        contadorPerfil = 0
    ))
    val state: LiveData<MainState> = _state


    fun handleChangeCheck()
    {
        _state.value = _state.value?.copy(checked = _state.value?.checked?.not() ?: false)
    }
    fun handleDameInicio(){
        val contadorActual = _state.value?.contadorInicio?.plus(1) ?: 0
        val mensaje = "Inicio ${contadorActual}"
        _state.value = _state.value?.copy(inicioText = mensaje , contadorInicio = contadorActual)
    }

    fun handleDameTareas(){
        val contadorActual = _state.value?.contadorTareas?.plus(1) ?: 0
        val mensaje = "Tareas ${contadorActual}"
        _state.value = _state.value?.copy(tareasText = mensaje , contadorTareas = contadorActual)
    }

    fun handleDamePerfil() {
        val contadorActual = _state.value?.contadorPerfil?.plus(1) ?: 0
        val mensaje = "Perfil ${contadorActual}"
        _state.value = _state.value?.copy(perfilText = mensaje , contadorPerfil = contadorActual)
    }

    fun handleDameEstadisticas() {
        val contadorActual = _state.value?.contadorEstadisticas?.plus(1) ?: 0
        val mensaje = "Estadisticas ${contadorActual}"
        _state.value = _state.value?.copy(estadisticasText = mensaje , contadorEstadisticas = contadorActual)
    }

    class MainViewModelFactory(
        private val dameInicioUseCase: DameInicioUseCase,
        private val dameTareasUseCase: DameTareasUseCase,
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return MainViewModel(
                    dameInicioUseCase,
                    dameTareasUseCase,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}

