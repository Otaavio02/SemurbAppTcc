package com.otavioaugusto.app_semurb.funcoes
import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.adapters.AvariasAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassAvariaItem

class AvariasRecyclerHelper(
    private val context: Context,
    private val recyclerView: RecyclerView,
    onFotoClick: (position: Int) -> Unit
) {
    private val avariasList = mutableListOf(DataClassAvariaItem())
    private val adapter = AvariasAdapter(avariasList, onFotoClick)

    init {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun getAvarias(): List<DataClassAvariaItem> = avariasList

    fun addAvaria(avaria: DataClassAvariaItem) {
        avariasList.add(avaria)
        adapter.notifyItemInserted(avariasList.size - 1)
    }

    fun removeAvaria(index: Int) {
        if (index in avariasList.indices) {
            avariasList.removeAt(index)
            adapter.notifyItemRemoved(index)

        }
    }

    fun getAdapter(): AvariasAdapter = adapter

    fun atualizarFoto(posicao: Int, novaUri: Uri){
        if (posicao in avariasList.indices){
            avariasList[posicao].uriFoto = novaUri
            adapter.notifyItemChanged(posicao)
        }
    }
}


