package com.example.pruebajuansito.di

import com.example.pruebajuansito.domain.useCases.AddUseCase
import com.example.pruebajuansito.domain.useCases.DameListaUseCase
import com.example.pruebajuansito.domain.useCases.DameUserUseCase
import com.example.pruebajuansito.domain.useCases.DelUseCase

object AppModule {
    val dameUserUseCase : DameUserUseCase = DameUserUseCase()
    val dameListaUseCase : DameListaUseCase = DameListaUseCase()
    val addUseCase : AddUseCase = AddUseCase()
    val delUseCase : DelUseCase = DelUseCase()
}