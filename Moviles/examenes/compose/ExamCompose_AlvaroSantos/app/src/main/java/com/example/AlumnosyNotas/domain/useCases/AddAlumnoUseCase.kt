package com.example.AlumnosyNotas.domain.useCases

import com.example.AlumnosyNotas.data.AlumnosRepository
import com.example.AlumnosyNotas.domain.model.Alumno

class AddAlumnoUseCase {
    fun addAlumno(alumno: Alumno) = AlumnosRepository.addAlumno(alumno)
}
