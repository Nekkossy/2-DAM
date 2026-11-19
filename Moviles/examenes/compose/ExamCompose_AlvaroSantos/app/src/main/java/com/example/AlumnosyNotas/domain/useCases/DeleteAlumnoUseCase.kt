package com.example.AlumnosyNotas.domain.useCases

import com.example.AlumnosyNotas.data.AlumnosRepository

class DeleteAlumnoUseCase {
    fun deleteAlumno(id: Long) = AlumnosRepository.deleteAlumno(id)
}
