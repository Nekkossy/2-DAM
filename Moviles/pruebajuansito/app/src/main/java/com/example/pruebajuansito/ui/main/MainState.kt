package com.example.pruebajuansito.ui.main

import com.example.pruebajuansito.domain.model.Users

data class MainState(
    val users: String,
    val usuarios: List<Users>,
    val error: String? = null,
)