package com.otavioaugusto.app_semurb.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistoricoListItem
import com.otavioaugusto.app_semurb.databinding.ItemHistoricoBinding
import com.otavioaugusto.app_semurb.fragments.OcorrenciasFragment

class HistoricoAdapter(
    private val items: List<DataClassHistoricoListItem>,
    private val onItemClick: (DataClassHistorico) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHeader: TextView = itemView.findViewById(R.id.textDiaHeader)
    }

    inner class ItemViewHolder(val binding: ItemHistoricoBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(historico: DataClassHistorico, onClick: (DataClassHistorico) -> Unit) {

            val subtitulo = when (historico.topico) {
                "Atendimento de Ocorrências" -> "${historico.qtd_itens} ocorrência(s) enviada(s)"
                "Serviço Viário" -> "${historico.qtd_itens} sinalização(ões) enviada(s)"
                "Inspeção da Viatura" -> "${historico.qtd_itens} avaria(s)"
                else -> "${historico.qtd_itens} item(ns)"
            }

            val tituloEncurtado = when (historico.topico) {
                "Atendimento de Ocorrências" -> "Atend. de Ocorrências"
                "Serviço Viário" -> "Serviço Viário"
                "Inspeção da Viatura" -> "Inspeção da Viatura"
                else -> "Tópico não identificado"
            }

            binding.textViewTituloHistorico1.text = tituloEncurtado
            binding.textViewHorarioHistorico1.text = historico.horario_envio
            binding.textViewTamanhoHistorico1.text = subtitulo

            binding.root.setOnClickListener {
                onClick(historico)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DataClassHistoricoListItem.Header -> TYPE_HEADER
            is DataClassHistoricoListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_historico_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val binding = ItemHistoricoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DataClassHistoricoListItem.Header -> {
                (holder as HeaderViewHolder).textHeader.text = "Dia ${item.data}"
            }

            is DataClassHistoricoListItem.Item -> {
                (holder as ItemViewHolder).bind(item.historico, onItemClick)
            }
        }
    }

    override fun getItemCount(): Int = items.size

}
