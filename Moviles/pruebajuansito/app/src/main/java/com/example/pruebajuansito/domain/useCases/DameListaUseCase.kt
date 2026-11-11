package com.example.pruebajuansito.domain.useCases

import com.example.pruebajuansito.data.RepositoryUsers

class DameListaUseCase {
    fun dameLista() = RepositoryUsers.dameLista()
}