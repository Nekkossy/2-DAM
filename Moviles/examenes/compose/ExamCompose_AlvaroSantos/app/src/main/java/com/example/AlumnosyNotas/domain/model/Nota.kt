package com.example.AlumnosyNotas.domain.model

data class Nota(
    val id: Long = 0L,
    val asignatura: String = "",
    val calificacion: Double = 0.0,
    val fecha: String = "",
    val observaciones: String = ""
)
