package com.example.examenpracticareciclerview.domain.useCases

import com.example.examenpracticareciclerview.data.RepositoryUsuario
import com.example.examenpracticareciclerview.domain.model.Usuario

class AddUsuarioUseCase {
    fun addUsuario(u: Usuario) = RepositoryUsuario.add(u)

}