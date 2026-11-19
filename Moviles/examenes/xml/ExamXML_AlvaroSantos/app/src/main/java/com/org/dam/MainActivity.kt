
package com.org.dam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examxml_alvarosantos.databinding.ActivityMainBinding
import com.org.dam.adapter.AlumnoAdapter
import com.org.dam.model.Alumno

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: AlumnoAdapter

    private val alumnos = mutableListOf<Alumno>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        configRecycler()
        setupEventos()
    }

    private fun configRecycler() {

        binding.rvAlumnos.layoutManager = LinearLayoutManager(this)

        adapter = AlumnoAdapter { alumno ->
            alumnos.remove(alumno)
            adapter.submitList(alumnos.toList())
        }

        binding.rvAlumnos.adapter = adapter

        adapter.submitList(alumnos.toList())
    }

    private fun setupEventos() {

        binding.btnAnadir.setOnClickListener {

            val nombre = binding.etNombre.text.toString()
            val apellidos = binding.etApellidos.text.toString()
            val edad = binding.etEdad.text.toString().toIntOrNull()
            val email = binding.etEmail.text.toString()
            val curso = binding.etCurso.text.toString()

            if (nombre.isEmpty() || apellidos.isEmpty() ||
                edad == null || email.isEmpty() || curso.isEmpty()) {

                Toast.makeText(
                    this,
                    "Rellena todos los campos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val alumno = Alumno(
                id = (alumnos.maxOfOrNull { it.id } ?: 0) + 1,
                nombre = nombre,
                apellidos = apellidos,
                curso = curso,
                edad = edad,
                email = email,
                activo = true,
                notas = emptyList()
            )

            alumnos.add(alumno)

            adapter.submitList(alumnos.toList())

            // Limpiar formulario
            binding.etNombre.text.clear()
            binding.etApellidos.text.clear()
            binding.etEdad.text.clear()
            binding.etEmail.text.clear()
            binding.etCurso.text.clear()
        }
    }
}
