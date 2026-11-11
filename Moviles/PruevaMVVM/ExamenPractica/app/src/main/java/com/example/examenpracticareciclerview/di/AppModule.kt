package com.example.examenpracticareciclerview.di

import com.example.examenpracticareciclerview.domain.useCases.AddUsuarioUseCase
import com.example.examenpracticareciclerview.domain.useCases.CargarUsuarioUseCase
import com.example.examenpracticareciclerview.domain.useCases.DameUsuarioUseCase
import com.example.examenpracticareciclerview.domain.useCases.DelUsuarioUseCase

object AppModule {

    val addUsuarioUseCase: AddUsuarioUseCase = AddUsuarioUseCase()
    val cargarUsuarioUseCase: CargarUsuarioUseCase = CargarUsuarioUseCase()
    val delUsuarioUseCase: DelUsuarioUseCase = DelUsuarioUseCase()
    val dameUsuarioUseCase: DameUsuarioUseCase = DameUsuarioUseCase()
}