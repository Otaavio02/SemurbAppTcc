package com.otavioaugusto.app_semurb.adapters

import com.otavioaugusto.app_semurb.dataClasses.DataClassNotificacoes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.R


class NotificacoesAdapter(
    private val notificacoes: MutableList<DataClassNotificacoes>
): RecyclerView.Adapter<NotificacoesAdapter.ViewHolder>() {

    companion object{
        private const val TIPO_NAO_LIDA = 0
        private const val TIPO_LIDA = 1
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val titulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        val descricao: TextView = itemView.findViewById(R.id.textViewDescricao)
        val horario: TextView = itemView.findViewById(R.id.textViewHorario)
        val data: TextView = itemView.findViewById(R.id.textViewData)

    }

    override fun getItemViewType(position: Int): Int {
        return if (notificacoes[position].lida) TIPO_LIDA else TIPO_NAO_LIDA
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notificacoes[position]
        holder.titulo.text = item.titulo
        holder.descricao.text = item.descricao
        holder.horario.text = item.horario.toString()
        holder.data.text = item.data.toString()


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return if (viewType == TIPO_LIDA){
           val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notifacacaolida, parent, false)
           ViewHolder(view)
       } else {
           val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notificacaonaolida, parent, false)
           ViewHolder(view)
       }

    }
    override fun getItemCount() = notificacoes.size

    fun getItemAt(position: Int): DataClassNotificacoes = notificacoes[position]

    fun removeItemAt(position: Int): DataClassNotificacoes = notificacoes.removeAt(position).also { notifyItemRemoved(position) }

    fun addItem(dataClassNotificacoes: DataClassNotificacoes){
        notificacoes.add(dataClassNotificacoes)
        notifyItemInserted(notificacoes.size - 1)
    }

    fun clearAll(){
        notificacoes.clear()
        notifyDataSetChanged()
    }

}