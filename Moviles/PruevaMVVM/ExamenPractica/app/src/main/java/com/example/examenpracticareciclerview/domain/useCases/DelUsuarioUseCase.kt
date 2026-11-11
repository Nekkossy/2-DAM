package com.example.examenpracticareciclerview.domain.useCases

import com.example.examenpracticareciclerview.data.RepositoryUsuario
import com.example.examenpracticareciclerview.domain.model.Usuario

class DelUsuarioUseCase {

    fun delUsuario(u: Usuario) = RepositoryUsuario.del(u)

}