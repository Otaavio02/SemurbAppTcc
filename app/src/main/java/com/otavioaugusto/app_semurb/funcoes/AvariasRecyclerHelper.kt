package com.otavioaugusto.app_semurb.funcoes
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.adapters.AvariasAdapter
import com.otavioaugusto.app_semurb.dataClasses.AvariaItem

class AvariasRecyclerHelper(
    private val context: Context,
    private val recyclerView: RecyclerView
) {
    private val avariasList = mutableListOf(AvariaItem())
    private val adapter = AvariasAdapter(avariasList)

    init {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun getAvarias(): List<AvariaItem> = avariasList

    fun addAvaria(avaria: AvariaItem) {
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
}
