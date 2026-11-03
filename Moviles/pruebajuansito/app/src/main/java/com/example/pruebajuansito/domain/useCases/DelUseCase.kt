package com.example.pruebajuansito.domain.useCases

import com.example.pruebajuansito.data.RepositoryUsers
import com.example.pruebajuansito.domain.model.Users

class DelUseCase{
    fun del(u : Users) = RepositoryUsers.del(u)
}