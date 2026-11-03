package com.example.pruebajuansito.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pruebajuansito.domain.useCases.AddUseCase
import com.example.pruebajuansito.domain.useCases.DameListaUseCase
import com.example.pruebajuansito.domain.useCases.DameUserUseCase
import com.example.pruebajuansito.domain.useCases.DelUseCase

class MainViewModel(
    private val dameUserUseCase: DameUserUseCase,
    private val dameListaUseCase: DameListaUseCase,
    private val addUseCase: AddUseCase,
    private val delUseCase: DelUseCase,
) : ViewModel() {
    private val _state = MutableLiveData<MainState>(MainState("Lista de panchos", listOf()))
}