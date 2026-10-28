package com.example.pruebayeicof.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebayeicof.R
import com.example.pruebayeicof.databinding.ActivityMainBinding
import com.example.pruebayeicof.di.AppModule
import com.example.pruebayeicof.domain.model.Usuario

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel : MainViewModel by viewModels {
        MainViewModelFactory(
            AppModule.addUsuarioUseCase,
            AppModule.cargarUsuarioUseCase,
            AppModule.dameUsuarioUseCase,
            AppModule.delUsuarioUseCase
        )
    }

    private lateinit var adapter: UsuarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.handleCargarLista()

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configRecycler()

        observarEstado()

        setupEventos()
    }

    private fun configRecycler() {
        binding.listado.layoutManager = LinearLayoutManager(this)

        adapter = UsuarioAdapter({ usuario->
            viewModel.handleDelete(usuario)
        })
        binding.listado.adapter = adapter
    }

    private fun setupEventos(){
        with(binding){
            buttonShow.setOnClickListener {
                viewModel.handleDameUsuario()
                viewModel.handleCargarLista()
            }
            buttonAdd.setOnClickListener {
                var u = Usuario("Ethan", "Betancor", 21)

                viewModel.handleAdd(u)
            }

        }
    }

    private fun observarEstado(){
        viewModel.state.observe(this, { state ->
            state?.usuario.let {
                binding.textView.text = it
            }

            state?.usuarios.let {
                adapter.submitList(it)
            }

            state?.error?.let { error ->
                Toast.makeText(this,error, Toast.LENGTH_SHORT).show()
                viewModel.handleLimpiarError()
            }})
    }
}