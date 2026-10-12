package com.example.pruebayeicof.data

import com.example.pruebayeicof.domain.model.Usuario
import kotlin.random.Random

object RepositoryUsuario {
    private val usuarios = mutableListOf(
        Usuario("Jacob", "Rosales",22),
        Usuario("Alvaro", "Santos", 23)
    )

    fun dameUsuario() = usuarios[0]

    fun dameLista() = usuarios.toList()
    fun add(u: Usuario){
        if (Random.nextDouble()>0.9)
            throw RuntimeException("Error al añadir")
        usuarios.add(u)
    }

    fun del(u: Usuario) {
        if ( Random.nextDouble()> 0.9)
            throw RuntimeException("Error al borrar")
        usuarios.remove(u)

    }
}