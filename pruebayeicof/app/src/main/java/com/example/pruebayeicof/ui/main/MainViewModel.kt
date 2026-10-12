package com.example.pruebayeicof.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pruebayeicof.domain.model.Usuario
import com.example.pruebayeicof.domain.useCases.AddUsuarioUseCase
import com.example.pruebayeicof.domain.useCases.CargarUsuarioUseCase
import com.example.pruebayeicof.domain.useCases.DameUsuarioUseCase
import com.example.pruebayeicof.domain.useCases.DelUsuarioUseCase
import kotlin.jvm.java

class MainViewModel(
    private val addUsuarioUseCase: AddUsuarioUseCase,
    private val cargarUsuarioUseCase: CargarUsuarioUseCase,
    private val dameUsuarioUseCase: DameUsuarioUseCase,
    private val delUsuarioUseCase: DelUsuarioUseCase
) : ViewModel() {

    private val _state = MutableLiveData<MainState>(MainState("Gestion de Usuario", listOf()))
    val state: LiveData<MainState> = _state

    init {
        handleCargarLista()
    }

    fun handleDameUsuario() {
        var usur = dameUsuarioUseCase.dameUsuario()

        _state.value = _state.value?.copy(usuario = usur.toString())
    }

    fun handleCargarLista() {
        _state.value = _state.value?.copy(usuarios = cargarUsuarioUseCase.cargarLista())
    }

    fun handleAdd(u: Usuario) {
        try {
            addUsuarioUseCase.addUsuario(u)
            _state.value = _state.value?.copy(usuarios = cargarUsuarioUseCase.cargarLista())
        } catch (e: Exception) {
            _state.value = _state.value?.copy(error = e.message)
        }
    }

    fun handleDelete(u: Usuario) {

        try {
            delUsuarioUseCase.delUsuario(u)
            _state.value = _state.value?.copy(usuarios = cargarUsuarioUseCase.cargarLista())
        }
        catch (e: Exception){
            _state.value = _state.value?.copy(error= e.message)
        }

    }

    fun handleLimpiarError() {
        _state.value = _state.value?.copy(error = null)
    }

}

class MainViewModelFactory(
    val addUsuarioUseCase: AddUsuarioUseCase,
    val cargarUsuarioUseCase: CargarUsuarioUseCase,
    val dameUsuarioUseCase: DameUsuarioUseCase,
    val delUsuarioUseCase: DelUsuarioUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return MainViewModel(
                addUsuarioUseCase,
                cargarUsuarioUseCase,
                dameUsuarioUseCase,
                delUsuarioUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}