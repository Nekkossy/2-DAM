package com.example.pruebajuansito.domain.useCases

import com.example.pruebajuansito.data.RepositoryUsers
import com.example.pruebajuansito.domain.model.Users

class AddUseCase {
    fun add(u : Users) = RepositoryUsers.add(u)
}