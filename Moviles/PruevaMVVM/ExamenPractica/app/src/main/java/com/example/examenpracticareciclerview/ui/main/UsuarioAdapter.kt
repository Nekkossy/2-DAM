package com.example.examenpracticareciclerview.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.examenpracticareciclerview.databinding.ItemUsuarioBinding
import com.example.examenpracticareciclerview.domain.model.Usuario

class UsuarioAdapter(val delUsuario: (Usuario) -> Unit) :
    ListAdapter<Usuario, UsuarioAdapter.UsuarioViewHolder>(UsuarioDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsuarioViewHolder(binding, delUsuario)
    }

    override fun onBindViewHolder(
        holder: UsuarioViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class UsuarioViewHolder(
        private val binding: ItemUsuarioBinding,
        private val onClickDelete: (Usuario) -> Unit,

        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(usuario: Usuario) {

            with(binding) {
                txtNombre.text = usuario.nombre

                root.setOnClickListener {

                    onClickDelete(usuario)
                }
            }
        }
    }
}

class UsuarioDiffCallback : DiffUtil.ItemCallback<Usuario>() {
    override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean =
        oldItem.nombre == newItem.nombre

    override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean =
        oldItem == newItem
}
