package com.otavioaugusto.app_semurb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dataClasses.AvariaItem

class AvariasAdapter(
    private val avarias: MutableList<AvariaItem>,
    private val onFotoClick: (position: Int) -> Unit
) : RecyclerView.Adapter<AvariasAdapter.AvariaViewHolder>() {

    inner class AvariaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descricaoEditText: TextInputEditText? = itemView.findViewById(R.id.editTextDescricao)
        val btnAdicionar: ImageButton = itemView.findViewById(R.id.btnAdicionar)
        val btnFoto: ImageButton? = itemView.findViewById(R.id.btnFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvariaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avarias, parent, false)
        return AvariaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvariaViewHolder, position: Int) {
        val avaria = avarias[position]

        holder.descricaoEditText?.setText(avaria.descricao)

        holder.descricaoEditText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                avaria.descricao = holder.descricaoEditText.text.toString()
            }
        }

        if (position == avarias.size - 1) {
            holder.btnAdicionar.setImageResource(R.drawable.ic_add)
            holder.btnAdicionar.setOnClickListener {
                avarias.add(AvariaItem())
                notifyItemInserted(avarias.size - 1)
                notifyItemChanged(avarias.size - 2)
            }
        } else {
            holder.btnAdicionar.setImageResource(R.drawable.ic_remove)
            holder.btnAdicionar.setOnClickListener {
                avarias.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, avarias.size)
            }
        }

        holder.btnFoto?.setOnClickListener {
            onFotoClick(position)
        }
    }

    override fun getItemCount(): Int = avarias.size
}
