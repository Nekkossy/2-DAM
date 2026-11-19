package com.example.AlumnosyNotas.ui.main

import com.example.AlumnosyNotas.domain.model.Alumno

data class MainState(
    val alumnos: List<Alumno> = emptyList(),
    val alumno: Alumno = Alumno(),
    val alumnoSeleccionado: Alumno? = null,
    val error: String? = null,
    val mensaje: String? = null
)
