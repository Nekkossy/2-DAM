package com.example.AlumnosyNotas.di

import com.example.AlumnosyNotas.domain.useCases.AddAlumnoUseCase
import com.example.AlumnosyNotas.domain.useCases.CargarAlumnosUseCase
import com.example.AlumnosyNotas.domain.useCases.DeleteAlumnoUseCase

object AppModule {
    val addAlumnoUseCase = AddAlumnoUseCase()
    val cargarAlumnosUseCase = CargarAlumnosUseCase()
    val deleteAlumnoUseCase = DeleteAlumnoUseCase()
}
