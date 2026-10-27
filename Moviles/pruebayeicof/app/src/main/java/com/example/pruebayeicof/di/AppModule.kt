package com.example.pruebayeicof.di

import com.example.pruebayeicof.domain.useCases.AddUsuarioUseCase
import com.example.pruebayeicof.domain.useCases.CargarUsuarioUseCase
import com.example.pruebayeicof.domain.useCases.DameUsuarioUseCase
import com.example.pruebayeicof.domain.useCases.DelUsuarioUseCase

object AppModule {
    val addUsuarioUseCase: AddUsuarioUseCase = AddUsuarioUseCase()
    val cargarUsuarioUseCase: CargarUsuarioUseCase = CargarUsuarioUseCase()
    val delUsuarioUseCase: DelUsuarioUseCase = DelUsuarioUseCase()
    val dameUsuarioUseCase: DameUsuarioUseCase = DameUsuarioUseCase()

}