package com.otavioaugusto.app_semurb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistoricoListItem

class HistoricoAdapter(
    private val items: List<DataClassHistoricoListItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHeader: TextView = itemView.findViewById(R.id.textDiaHeader)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.textViewTituloHistorico1)
        val horario: TextView = itemView.findViewById(R.id.textViewHorarioHistorico1)
        val tamanho: TextView = itemView.findViewById(R.id.textViewTamanhoHistorico1)
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
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_historico, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DataClassHistoricoListItem.Header -> {
                (holder as HeaderViewHolder).textHeader.text = "Dia ${item.data}"
            }

            is DataClassHistoricoListItem.Item -> {
                val historico = item.historico
                val subtitulo = when (historico.topico) {
                    "Atendimento de Ocorrências" -> "${historico.qtd_itens} ocorrência(s) enviada(s)"
                    "Serviço Viário" -> "${historico.qtd_itens} sinalização(ões) enviada(s)"
                    "Inspeção da Viatura" -> "${historico.qtd_itens} avaria(s)"
                    else -> "${historico.qtd_itens} item(ns)"
                }

                with(holder as ItemViewHolder) {
                    titulo.text = historico.topico
                    horario.text = historico.horario_envio
                    tamanho.text = subtitulo
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

}
