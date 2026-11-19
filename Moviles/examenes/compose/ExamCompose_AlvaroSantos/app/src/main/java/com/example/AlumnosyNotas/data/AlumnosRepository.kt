package com.example.AlumnosyNotas.data

import com.example.AlumnosyNotas.domain.model.Alumno
import com.example.AlumnosyNotas.domain.model.Nota

object AlumnosRepository {
    private var siguienteIdAlumno = 6L

    private val alumnos = mutableListOf(
        Alumno(
            id = 1L,
            nombre = "Lucía",
            apellidos = "Álvarez García",
            curso = "1º DAM",
            edad = 18,
            email = "lucia.alvarez@iesquevedo.es",
            activo = true,
            notas = listOf(
                Nota(1L, "Programación", 8.5, "12/11/2026", "Buen trabajo con Kotlin"),
                Nota(2L, "Bases de datos", 7.0, "14/11/2026", "Repasar las consultas JOIN"),
                Nota(3L, "Sistemas", 9.0, "17/11/2026", "Examen práctico")
            )
        ),
        Alumno(
            id = 2L,
            nombre = "Marco",
            apellidos = "Castillo Ruiz",
            curso = "2º ASIR",
            edad = 19,
            email = "marco.castillo@iesquevedo.es",
            activo = true,
            notas = listOf(
                Nota(4L, "Redes", 6.5, "10/11/2026", "Práctica de direccionamiento"),
                Nota(5L, "Seguridad", 8.0, "15/11/2026", "Correcto")
            )
        ),
        Alumno(
            id = 3L,
            nombre = "Sofía",
            apellidos = "Pérez Martín",
            curso = "1º SMR",
            edad = 17,
            email = "sofia.perez@iesquevedo.es",
            activo = false,
            notas = listOf(
                Nota(6L, "Ofimática", 5.5, "11/11/2026", "Puede mejorar la presentación")
            )
        ),
        Alumno(
            id = 4L,
            nombre = "Daniel",
            apellidos = "Rodríguez López",
            curso = "2º DAM",
            edad = 20,
            email = "daniel.rodriguez@iesquevedo.es",
            activo = true,
            notas = listOf(
                Nota(7L, "Acceso a datos", 9.5, "13/11/2026", "Muy buen trabajo"),
                Nota(8L, "Interfaces", 8.5, "16/11/2026", "Diseño claro")
            )
        ),
        Alumno(
            id = 5L,
            nombre = "Elena",
            apellidos = "Moreno Sánchez",
            curso = "1º ASIR",
            edad = 18,
            email = "elena.moreno@iesquevedo.es",
            activo = true,
            notas = emptyList()
        )
    )

    fun getAlumnos(): List<Alumno> = alumnos.toList()

    fun addAlumno(alumno: Alumno) {
        alumnos.add(alumno.copy(id = siguienteIdAlumno++))
    }

    fun deleteAlumno(id: Long) {
        alumnos.removeAll { it.id == id }
    }
}
