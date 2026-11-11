package com.example.examenpracticareciclerview.domain.useCases

import com.example.examenpracticareciclerview.data.RepositoryUsuario

class CargarUsuarioUseCase {
    fun cargarLista() = RepositoryUsuario.dameLista()

}