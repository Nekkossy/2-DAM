package com.example.miprimercomposito

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class FormularioViewModel : ViewModel() {

    private val _nombre = mutableStateOf("")
    val nombre: State<String> = _nombre

    private val _apellidos = mutableStateOf("")
    val apellidos: State<String> = _apellidos

    private val _correo = mutableStateOf("")
    val correo: State<String> = _correo

    private val _contador = mutableStateOf(0)
    val contador: State<Int> = _contador

    fun cambiarNombre(valor: String) {
        _nombre.value = valor
    }

    fun cambiarApellidos(valor: String) {
        _apellidos.value = valor
    }

    fun cambiarCorreo(valor: String) {
        _correo.value = valor
    }

    fun registrarRecurso() {
        _contador.value++
    }
}
