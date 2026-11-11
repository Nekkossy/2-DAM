package com.example.examenpracticareciclerview.ui.main

import com.example.examenpracticareciclerview.domain.model.Usuario

data class MainState(
    val usuario: String,
    val usuarios: List<Usuario>,
    val error: String? = null,
)