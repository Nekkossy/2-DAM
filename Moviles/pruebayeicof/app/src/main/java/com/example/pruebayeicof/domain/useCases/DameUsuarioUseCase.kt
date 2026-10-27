package com.example.pruebayeicof.domain.useCases

import com.example.pruebayeicof.data.RepositoryUsuario

class DameUsuarioUseCase {
    fun dameUsuario() = RepositoryUsuario.dameUsuario()
}