package com.example.examenpantallasalejandro.di

import com.example.examenpantallasalejandro.domain.useCases.DameInicioUseCase
import com.example.examenpantallasalejandro.domain.useCases.DameTareasUseCase

object AppModule {

    val dameInicioUseCase: DameInicioUseCase = DameInicioUseCase()

    val dameTareasUseCase: DameTareasUseCase = DameTareasUseCase()
}