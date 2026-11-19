package com.example.AlumnosyNotas.domain.model

data class Alumno(
    val id: Long = 0L,
    val nombre: String = "",
    val apellidos: String = "",
    val curso: String = "1º DAM",
    val edad: Int = 0,
    val email: String = "",
    val activo: Boolean = true,
    val notas: List<Nota> = emptyList()
)
