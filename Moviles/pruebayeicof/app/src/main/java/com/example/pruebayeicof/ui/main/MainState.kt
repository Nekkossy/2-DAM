package com.example.pruebayeicof.ui.main

import com.example.pruebayeicof.domain.model.Usuario

data class MainState(
    val usuario: String,
    val usuarios: List<Usuario>,
    val error: String? = null,
)