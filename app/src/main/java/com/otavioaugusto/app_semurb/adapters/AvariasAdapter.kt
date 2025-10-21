package com.otavioaugusto.app_semurb.adapters

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dataClasses.DataClassAvariaItem
import com.squareup.picasso.Picasso

class AvariasAdapter(
    private val avarias: MutableList<DataClassAvariaItem>,
    private val onFotoClick: (position: Int) -> Unit,
    private val modoHistorico: Boolean = false
) : RecyclerView.Adapter<AvariasAdapter.AvariaViewHolder>() {

    inner class AvariaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descricaoEditText: TextInputEditText = itemView.findViewById(R.id.editTextDescricao)
        val btnAdicionar: ImageButton = itemView.findViewById(R.id.btnAdicionar)
        val btnFoto: ImageButton = itemView.findViewById(R.id.btnFoto)

        val imageFoto: ImageView = itemView.findViewById(R.id.imageFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvariaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avarias, parent, false)
        return AvariaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvariaViewHolder, position: Int) {
        val avaria = avarias[position]


        holder.descricaoEditText.setText(avaria.descricao)


        // Se for modo histórico, desativa tudo
        if (modoHistorico == true) {
            holder.descricaoEditText.isEnabled = false
            holder.btnAdicionar.visibility = View.GONE
            holder.btnFoto.isEnabled = false
        } else {
            holder.descricaoEditText.isEnabled = true
            // comportamento normal de adicionar/remover
            if (position == avarias.size - 1) {
                holder.btnAdicionar.setImageResource(R.drawable.ic_add)
                holder.btnAdicionar.setOnClickListener {
                    avarias.add(DataClassAvariaItem())
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
        }

        if (avaria.uriFoto != null) {
            if (modoHistorico == true) {
                Picasso.get()
                    .load(avaria.uriFoto)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_picture)
                    .into(holder.imageFoto, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            Log.i("Picasso", "Imagem carregada com sucesso")
                        }

                        override fun onError(e: Exception?) {
                            //Toast.makeText(hol, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
                            Log.e("Picasso", "Erro no carregamento da imagem", e)
                        }
                    })

            } else {
                holder.imageFoto.visibility = View.VISIBLE
                holder.imageFoto.setImageURI(avaria.uriFoto)
                holder.btnFoto.isEnabled = false
                holder.btnFoto.visibility = View.GONE

                holder.imageFoto.setOnClickListener {
                    val context = holder.itemView.context
                    val dialogView = ImageView(context).apply {
                        setImageURI(avaria.uriFoto)
                        adjustViewBounds = true
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }

                    AlertDialog.Builder(context)
                        .setView(dialogView)
                        .setPositiveButton("Fechar", null)
                        .show()
                }
            }

        } else {
            if (modoHistorico == true){
                holder.btnFoto.visibility = View.GONE
                holder.imageFoto.visibility = View.GONE
            } else {
                holder.imageFoto.visibility = View.INVISIBLE
                holder.imageFoto.setImageResource(R.drawable.ic_camera)
                holder.btnFoto.isEnabled = true
                holder.btnFoto.visibility = View.VISIBLE

                holder.btnFoto.setOnClickListener {
                    onFotoClick(position)
                }

                holder.imageFoto.setOnClickListener(null)
            }
        }
    }
    fun getLista(): List<DataClassAvariaItem> = avarias

    override fun getItemCount(): Int = avarias.size
}
