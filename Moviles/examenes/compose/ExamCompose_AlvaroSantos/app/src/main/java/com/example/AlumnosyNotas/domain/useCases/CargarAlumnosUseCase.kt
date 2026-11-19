package com.example.AlumnosyNotas.domain.useCases

import com.example.AlumnosyNotas.data.AlumnosRepository

class CargarAlumnosUseCase {
    fun cargarAlumnos() = AlumnosRepository.getAlumnos()
}
