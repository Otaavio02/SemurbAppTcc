package com.otavioaugusto.app_semurb.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.dataClasses.DataClassViario
import com.otavioaugusto.app_semurb.databinding.ItemOcorrenciaviarioBinding

class ViarioAdapter : RecyclerView.Adapter<ViarioAdapter.ViewHolder>() {

    private val lista = mutableListOf<DataClassViario>()

    fun submitList(novaLista: List<DataClassViario>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemOcorrenciaviarioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataClassViario) {
            binding.textTitulo.text = "Sinalização ${item.numeroSequencial}"
            binding.textTipo.text = item.tipo ?: ""
            binding.textEndereco.text = item.endereco ?: ""

            binding.btnEditarOcoVia.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, PlaceHolderGameficadoActivity::class.java)
                intent.putExtra("FRAGMENT_KEY", "VIARIO_EDITADO")
                intent.putExtra("VISIBILITY", "GONE")

                intent.putExtra("ID_VIARIO", item.id.toLong())
                intent.putExtra("TIPO", item.tipo)
                intent.putExtra("ENDERECO", item.endereco)
                intent.putExtra("DESCRICAO", item.descricao)

                context.startActivity(intent)
            }

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