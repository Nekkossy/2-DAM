package com.example.examenpantallasalejandro.ui

data class MainState(
    val checked:Boolean,
    val inicioText: String,
    val tareasText: String,
    val estadisticasText: String,
    val perfilText: String,
    val contadorInicio:Int,
    val contadorTareas:Int,
    val contadorEstadisticas:Int,
    val contadorPerfil:Int,
    val error: String? = null,
)
