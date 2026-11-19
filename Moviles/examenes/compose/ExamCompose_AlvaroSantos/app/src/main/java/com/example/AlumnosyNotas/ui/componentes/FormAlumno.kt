package com.example.AlumnosyNotas.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.AlumnosyNotas.domain.model.Alumno
import com.example.AlumnosyNotas.ui.main.MainState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAlumno(
    state: MainState,
    onChangeAlumno: (Alumno) -> Unit,
    onAddAlumno: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cursos = listOf("1º DAM", "2º DAM", "1º ASIR", "2º ASIR", "1º SMR", "2º SMR")
    var cursoExpanded by remember { mutableStateOf(false) }

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Añadir nuevo alumno",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.alumno.nombre,
                    onValueChange = { onChangeAlumno(state.alumno.copy(nombre = it)) },
                    label = { Text("Nombre") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.alumno.apellidos,
                    onValueChange = { onChangeAlumno(state.alumno.copy(apellidos = it)) },
                    label = { Text("Apellidos") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = cursoExpanded,
                    onExpandedChange = { cursoExpanded = !cursoExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.alumno.curso,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = cursoExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = cursoExpanded,
                        onDismissRequest = { cursoExpanded = false }
                    ) {
                        cursos.forEach { curso ->
                            DropdownMenuItem(
                                text = { Text(curso) },
                                onClick = {
                                    onChangeAlumno(state.alumno.copy(curso = curso))
                                    cursoExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = if (state.alumno.edad == 0) "" else state.alumno.edad.toString(),
                    onValueChange = { texto ->
                        onChangeAlumno(state.alumno.copy(edad = texto.toIntOrNull() ?: 0))
                    },
                    label = { Text("Edad") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.alumno.email,
                onValueChange = { onChangeAlumno(state.alumno.copy(email = it)) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.alumno.activo,
                    onCheckedChange = { onChangeAlumno(state.alumno.copy(activo = it)) }
                )
                Text("Alumno activo")
            }

            Button(
                onClick = onAddAlumno,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Añadir alumno")
            }
        }
    }
}
