package com.example.examenpantallasalejandro.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.examenpantallasalejandro.R
import com.example.examenpantallasalejandro.databinding.ActivityMainBinding
import com.example.examenpantallasalejandro.di.AppModule

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            AppModule.dameInicioUseCase,
            AppModule.dameTareasUseCase,
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observarEstado()

        setUpEventos()
    }

    private fun setUpEventos(){
        with(binding){
            buttonEditar.setOnClickListener {
                viewModel.handleChangeCheck()
            }
            buttonInicio.setOnClickListener {
                viewModel.handleDameInicio()
            }
            buttonTareas.setOnClickListener {
                viewModel.handleDameTareas()
            }
            buttonPerfil.setOnClickListener {
                viewModel.handleDamePerfil()
            }
            buttonEstadisticas.setOnClickListener {
                viewModel.handleDameEstadisticas()
            }
        }
    }

    private fun observarEstado() {
        viewModel.state.observe(this,{state ->
            with(binding) {
                state?.checked.let { checked ->
                  checkboxChild1.isChecked = checked == true
                  checkboxChild2.isChecked = checked == true
                  checkboxChild3.isChecked = checked == true
                  checkboxChild4.isChecked = checked == true
                }
                state?.inicioText.let {
                    TextTask1.text = it
                }
                state?.perfilText.let {
                    TextTask3.text = it
                }
                state?.tareasText.let {
                    TextTask2.text = it
                }
                state?.estadisticasText.let {
                    TextTask4.text = it
                }
            }
        } )
    }

}