package com.otavioaugusto.app_semurb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.dataClasses.DataViario
import com.otavioaugusto.app_semurb.databinding.ItemOcorrenciaviarioBinding

class ViarioAdapter : RecyclerView.Adapter<ViarioAdapter.ViewHolder>() {

    private val lista = mutableListOf<DataViario>()

    fun submitList(novaLista: List<DataViario>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemOcorrenciaviarioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataViario) {
            binding.textTitulo.text = "Sinalização ${item.id}"
            binding.textTipo.text = item.tipo ?: ""
            binding.textEndereco.text = item.endereco ?: ""

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOcorrenciaviarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }
}