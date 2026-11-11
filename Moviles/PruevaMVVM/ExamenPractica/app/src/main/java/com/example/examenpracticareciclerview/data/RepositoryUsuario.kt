package com.example.examenpracticareciclerview.data

import com.example.examenpracticareciclerview.domain.model.Usuario

object RepositoryUsuario {
    private val usuarios = mutableListOf(
        Usuario("Lucía", "Álvarez García", 1, "DAM", 18, "lucia.alvarez@iesquevedo.es", "activo"),
        Usuario("Marco", "Castillo Ruiz", 2, "ASIR", 19, "lucia.alvarez@iesquevedo.es", "activo"),
        Usuario("Sofía", "Pérez Martín", 1, "SMR", 17, "lucia.alvarez@iesquevedo.es", "inactivo"),
        Usuario("Daniel", "Rodríguez López", 2, "DAM", 20, "lucia.alvarez@iesquevedo.es", "activo"),
        Usuario("Elena", "Moreno Sánchez", 1, "ASIR", 18, "lucia.alvarez@iesquevedo.es", "activo"),
    )

    fun dameUsuario() = usuarios[0]

    fun dameLista() = usuarios.toList()

    fun add(u: Usuario){
        usuarios.add(u)
    }

    fun del(u: Usuario) {
        usuarios.remove(u)

    }
}