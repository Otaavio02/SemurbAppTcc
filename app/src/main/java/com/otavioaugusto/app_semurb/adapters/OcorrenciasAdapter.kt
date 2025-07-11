package com.otavioaugusto.app_semurb.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.dataClasses.DataClassOcorrencia
import com.otavioaugusto.app_semurb.databinding.ItemOcorrenciaviarioBinding

class OcorrenciasAdapter : RecyclerView.Adapter<OcorrenciasAdapter.ViewHolder>() {

    private val lista = mutableListOf<DataClassOcorrencia>()

    fun submitList(novaLista: List<DataClassOcorrencia>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemOcorrenciaviarioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataClassOcorrencia) {
            binding.textTitulo.text = "Ocorrência ${item.numeroSequencial}"
            binding.textTipo.text = item.tipo
            binding.textEndereco.text = item.endereco

            binding.btnEditarOcoVia.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, PlaceHolderGameficadoActivity::class.java)
                intent.putExtra("FRAGMENT_KEY", "OCORRENCIAS_EDITADO")
                intent.putExtra("VISIBILITY", "GONE")
                intent.putExtra("ID_OCORRENCIA", item.id.toLong())
                intent.putExtra("TIPO", item.tipo)
                intent.putExtra("ENDERECO", item.endereco)
                intent.putExtra("NOME", item.nome)
                intent.putExtra("CONTATO", item.numcontato)

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
