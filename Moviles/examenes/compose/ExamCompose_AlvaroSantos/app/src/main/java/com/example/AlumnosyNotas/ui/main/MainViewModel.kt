package com.example.AlumnosyNotas.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.AlumnosyNotas.domain.model.Alumno
import com.example.AlumnosyNotas.domain.useCases.AddAlumnoUseCase
import com.example.AlumnosyNotas.domain.useCases.CargarAlumnosUseCase
import com.example.AlumnosyNotas.domain.useCases.DeleteAlumnoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val addAlumnoUseCase: AddAlumnoUseCase,
    private val cargarAlumnosUseCase: CargarAlumnosUseCase,
    private val deleteAlumnoUseCase: DeleteAlumnoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val listaInicial = cargarAlumnosUseCase.cargarAlumnos()
            _state.update { it.copy(alumnos = listaInicial) }
        }
    }

    fun onChangeAlumno(alumnoForm: Alumno) {
        _state.update {
            it.copy(
                alumno = alumnoForm,
                error = null,
                mensaje = null
            )
        }
    }

    fun onAddAlumno() {
        viewModelScope.launch {
            val form = _state.value.alumno

            if (form.nombre.isBlank()) {
                _state.update { it.copy(error = "El nombre es obligatorio", mensaje = null) }
                return@launch
            }
            if (form.apellidos.isBlank()) {
                _state.update { it.copy(error = "Los apellidos son obligatorios", mensaje = null) }
                return@launch
            }
            if (form.edad <= 0) {
                _state.update { it.copy(error = "Introduce una edad válida", mensaje = null) }
                return@launch
            }
            if (!form.email.contains("@")) {
                _state.update { it.copy(error = "Introduce un email válido", mensaje = null) }
                return@launch
            }

            addAlumnoUseCase.addAlumno(form.copy(id = 0L, notas = emptyList()))
            val listaActualizada = cargarAlumnosUseCase.cargarAlumnos()

            _state.update {
                it.copy(
                    alumnos = listaActualizada,
                    alumno = Alumno(),
                    error = null,
                    mensaje = "Alumno añadido"
                )
            }
        }
    }

    fun onSelectAlumno(alumno: Alumno) {
        _state.update { actual ->
            actual.copy(
                alumnoSeleccionado = actual.alumnos.firstOrNull { it.id == alumno.id } ?: alumno,
                error = null,
                mensaje = null
            )
        }
    }

    fun onClearSelection() {
        _state.update { it.copy(alumnoSeleccionado = null) }
    }

    fun onDeleteAlumno() {
        val seleccionado = _state.value.alumnoSeleccionado

        if (seleccionado == null) {
            _state.update {
                it.copy(error = "Selecciona un alumno primero", mensaje = null)
            }
            return
        }

        viewModelScope.launch {
            deleteAlumnoUseCase.deleteAlumno(seleccionado.id)
            val listaActualizada = cargarAlumnosUseCase.cargarAlumnos()

            _state.update {
                it.copy(
                    alumnos = listaActualizada,
                    alumnoSeleccionado = null,
                    error = null,
                    mensaje = "Alumno eliminado"
                )
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, mensaje = null) }
    }
}

class MainViewModelFactory(
    private val addAlumnoUseCase: AddAlumnoUseCase,
    private val cargarAlumnosUseCase: CargarAlumnosUseCase,
    private val deleteAlumnoUseCase: DeleteAlumnoUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                addAlumnoUseCase,
                cargarAlumnosUseCase,
                deleteAlumnoUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
