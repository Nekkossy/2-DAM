package com.example.AlumnosyNotas.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.AlumnosyNotas.di.AppModule
import com.example.AlumnosyNotas.domain.model.Alumno
import com.example.AlumnosyNotas.ui.componentes.AlumnoItemCard
import com.example.AlumnosyNotas.ui.componentes.DetalleAlumnoCard
import com.example.AlumnosyNotas.ui.componentes.FormAlumno
import com.example.AlumnosyNotas.ui.theme.AlumnosNotasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlumnosNotasTheme {
                MainPantalla()
            }
        }
    }
}

@Composable
fun MainPantalla(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            addAlumnoUseCase = AppModule.addAlumnoUseCase,
            cargarAlumnosUseCase = AppModule.cargarAlumnosUseCase,
            deleteAlumnoUseCase = AppModule.deleteAlumnoUseCase
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainPantallaContent(
        modifier = modifier,
        state = state,
        onChangeAlumno = viewModel::onChangeAlumno,
        onAddAlumno = viewModel::onAddAlumno,
        onSelectAlumno = viewModel::onSelectAlumno,
        onClearSelection = viewModel::onClearSelection,
        onDeleteAlumno = viewModel::onDeleteAlumno,
        onClearMessages = viewModel::clearMessages
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPantallaContent(
    modifier: Modifier = Modifier,
    state: MainState,
    onChangeAlumno: (Alumno) -> Unit,
    onAddAlumno: () -> Unit,
    onSelectAlumno: (Alumno) -> Unit,
    onClearSelection: () -> Unit,
    onDeleteAlumno: () -> Unit,
    onClearMessages: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }
    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            item {
                Text(
                    text = "Lista de alumnos (${state.alumnos.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            items(
                items = state.alumnos,
                key = { it.id }
            ) { alumno ->
                AlumnoItemCard(
                    alumno = alumno,
                    seleccionado = state.alumnoSeleccionado?.id == alumno.id,
                    onClick = { onSelectAlumno(alumno) }
                )
            }

            val alumnoSeleccionado = state.alumnoSeleccionado
            if (alumnoSeleccionado != null) {
                item(key = "detalle-alumno") {
                    DetalleAlumnoCard(
                        alumno = alumnoSeleccionado,
                        onCerrar = onClearSelection
                    )
                }
            }

            item {
                FormAlumno(
                    state = state,
                    onChangeAlumno = onChangeAlumno,
                    onAddAlumno = onAddAlumno,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = onDeleteAlumno,
                    enabled = state.alumnoSeleccionado != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text("Borrar alumno seleccionado")
                }
            }
        }
    }
}


@Composable
fun MainPantalla2(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            addAlumnoUseCase = AppModule.addAlumnoUseCase,
            cargarAlumnosUseCase = AppModule.cargarAlumnosUseCase,
            deleteAlumnoUseCase = AppModule.deleteAlumnoUseCase
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainPantallaContent2(
        modifier = modifier,
        state = state,
        onChangeAlumno = viewModel::onChangeAlumno,
        onAddAlumno = viewModel::onAddAlumno,
        onSelectAlumno = viewModel::onSelectAlumno,
        onClearSelection = viewModel::onClearSelection,
        onDeleteAlumno = viewModel::onDeleteAlumno,
        onClearMessages = viewModel::clearMessages
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPantallaContent2(
    modifier: Modifier = Modifier,
    state: MainState,
    onChangeAlumno: (Alumno) -> Unit,
    onAddAlumno: () -> Unit,
    onSelectAlumno: (Alumno) -> Unit,
    onClearSelection: () -> Unit,
    onDeleteAlumno: () -> Unit,
    onClearMessages: () -> Unit = {}
) {
    // Controlamos qué apartado se muestra
    var mostrarLista by remember { mutableStateOf(true) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {

            // Título y botones
            item {
                Text(
                    text = "Lista de alumnos (${state.alumnos.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            mostrarLista = true
                            mostrarFormulario = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ver alumnos")
                    }

                    Button(
                        onClick = {
                            mostrarLista = false
                            mostrarFormulario = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Añadir alumno")
                    }
                }
            }

            // Mostrar lista de alumnos
            if (mostrarLista) {

                items(
                    items = state.alumnos,
                    key = { it.id }
                ) { alumno ->

                    AlumnoItemCard(
                        alumno = alumno,
                        seleccionado = state.alumnoSeleccionado?.id == alumno.id,
                        onClick = {
                            onSelectAlumno(alumno)
                        }
                    )
                }

                // Mostrar detalles del alumno seleccionado
                val alumnoSeleccionado = state.alumnoSeleccionado

                if (alumnoSeleccionado != null) {
                    item(key = "detalle-alumno") {
                        DetalleAlumnoCard(
                            alumno = alumnoSeleccionado,
                            onCerrar = onClearSelection
                        )
                    }
                }

                // Botón para borrar
                item {
                    Button(
                        onClick = onDeleteAlumno,
                        enabled = state.alumnoSeleccionado != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.size(8.dp))

                        Text("Borrar alumno seleccionado")
                    }
                }
            }

            // Mostrar formulario para añadir alumno
            if (mostrarFormulario) {
                item {
                    FormAlumno(
                        state = state,
                        onChangeAlumno = onChangeAlumno,
                        onAddAlumno = onAddAlumno,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}






@Preview(showBackground = true)
@Composable
fun MainPantallaPreview() {
    AlumnosNotasTheme {
          MainPantalla()
    }
}

@Preview(showBackground = true)
@Composable
fun MainPantallaPreview2() {
    AlumnosNotasTheme {
        MainPantalla2()
    }
}