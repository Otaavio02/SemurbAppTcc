package com.otavioaugusto.app_semurb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.R

import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico1Ocorrencias
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico1Viario
import com.otavioaugusto.app_semurb.dataClasses.DataClassViario

class HistoricoAdapter(
    private val historicoListaOcorrencias: MutableList<DataClassHistorico1Ocorrencias>,
    private val historicoListaViario: MutableList<DataClassHistorico1Viario>
) : RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    inner class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.textViewTituloHistorico1)
        val horario: TextView = itemView.findViewById(R.id.textViewHorarioHistorico1)
        val tamanho: TextView = itemView.findViewById(R.id.textViewTamanhoHistorico1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico, parent, false)
        return HistoricoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val historicoOcorrencia = historicoListaOcorrencias[position]
        holder.titulo.text = historicoOcorrencia.titulo
        holder.horario.text = historicoOcorrencia.horarioEnvio
        holder.tamanho.text = "${historicoOcorrencia.quantidadeTotal} ocorrência(s) enviada(s)"
    }

    override fun getItemCount(): Int = historicoListaOcorrencias.size

    fun adicionarItem(item: DataClassHistorico1Ocorrencias) {
        historicoListaOcorrencias.add(0, item)
        notifyItemInserted(0)
    }
}
