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
    private val avarias: MutableList<AvariaItem>
) : RecyclerView.Adapter<AvariasAdapter.AvariaViewHolder>() {

    inner class AvariaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descricaoEditText = itemView.findViewById<TextInputEditText>(R.id.editTextDescricao)
        val btnAdicionar = itemView.findViewById<ImageButton>(R.id.btnAdicionar)
        val btnFoto = itemView.findViewById<ImageButton>(R.id.btnFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvariaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avarias, parent, false)
        return AvariaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvariaViewHolder, position: Int) {
        val avaria = avarias[position]

        holder.descricaoEditText.setText(avaria.descricao)

        holder.descricaoEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                avaria.descricao = holder.descricaoEditText.text.toString()
            }
        }

        holder.btnAdicionar.setOnClickListener {
            avarias.add(AvariaItem())
            notifyItemInserted(avarias.size - 1)
        }

        holder.btnFoto.setOnClickListener {

        }
    }


    override fun getItemCount(): Int = avarias.size
}