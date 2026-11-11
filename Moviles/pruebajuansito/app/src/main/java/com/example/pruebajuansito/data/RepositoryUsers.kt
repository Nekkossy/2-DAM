package com.example.pruebajuansito.data

import com.example.pruebajuansito.domain.model.Users

object RepositoryUsers {
    private val usuarios = mutableListOf(
        Users("Juan",22),
        Users("Alvaro", 23)
    )

    fun dameUser()  = usuarios[0]

    fun dameLista() = usuarios.toList()

    fun add(u: Users){
        usuarios.add(u)
    }

    fun del(u : Users){
        usuarios.remove(u)
    }
}
