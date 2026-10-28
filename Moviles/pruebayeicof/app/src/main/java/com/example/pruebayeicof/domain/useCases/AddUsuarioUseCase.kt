package com.example.pruebayeicof.domain.useCases

import com.example.pruebayeicof.data.RepositoryUsuario
import com.example.pruebayeicof.domain.model.Usuario

class AddUsuarioUseCase {
    fun addUsuario(u: Usuario) = RepositoryUsuario.add(u)
}