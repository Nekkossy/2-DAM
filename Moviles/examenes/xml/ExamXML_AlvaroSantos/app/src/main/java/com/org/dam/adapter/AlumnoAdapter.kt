
package com.org.dam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.examxml_alvarosantos.databinding.ItemAlumnoBinding
import com.org.dam.model.Alumno

class AlumnoAdapter(
    val borrarAlumno: (Alumno) -> Unit
) : ListAdapter<Alumno, AlumnoAdapter.AlumnoViewHolder>(
    AlumnoDiffCallback()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlumnoViewHolder {

        val binding = ItemAlumnoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return AlumnoViewHolder(binding, borrarAlumno)
    }

    override fun onBindViewHolder(
        holder: AlumnoViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class AlumnoViewHolder(
        private val binding: ItemAlumnoBinding,
        private val onClickDelete: (Alumno) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alumno: Alumno) {

            with(binding) {

                tvNombre.text = "${alumno.nombre} ${alumno.apellidos}"
                tvCurso.text = alumno.curso

                root.setOnClickListener {
                    onClickDelete(alumno)
                }
            }
        }
    }
}

class AlumnoDiffCallback : DiffUtil.ItemCallback<Alumno>() {

    override fun areItemsTheSame(
        oldItem: Alumno,
        newItem: Alumno
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Alumno,
        newItem: Alumno
    ): Boolean {
        return oldItem == newItem
    }
}
