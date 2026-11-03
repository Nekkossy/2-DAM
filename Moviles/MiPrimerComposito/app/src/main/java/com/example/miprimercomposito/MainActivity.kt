package com.example.miprimercomposito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miprimercomposito.ui.theme.MiPrimerCompositoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiPrimerCompositoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormularioScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FormularioScreen(
    modifier: Modifier = Modifier,
    viewModel: FormularioViewModel = viewModel()
) {

    val nombre = viewModel.nombre.value
    val apellidos = viewModel.apellidos.value
    val correo = viewModel.correo.value
    val contador = viewModel.contador.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Registro de alumnos"
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { viewModel.cambiarNombre(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellidos,
            onValueChange = { viewModel.cambiarApellidos(it) },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { viewModel.cambiarCorreo(it) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.registrarRecurso()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar alumno")
        }

        Text(
            text = "Recursos registrados: $contador"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FormularioPreview() {
    MiPrimerCompositoTheme {
        FormularioScreen(
            viewModel = viewModel()
        )
    }
}