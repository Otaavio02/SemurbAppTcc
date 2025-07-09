package com.otavioaugusto.app_semurb.fragments


import android.os.Bundle
import android.transition.AutoTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import android.transition.TransitionManager
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.NotificacoesAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassNotificacoes
import com.otavioaugusto.app_semurb.databinding.FragmentNotificacoesBinding
import java.time.LocalDate
import java.time.LocalTime

class NotificacoesFragment : Fragment() {

    private var _binding: FragmentNotificacoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterN: NotificacoesAdapter
    private lateinit var adapterLidas: NotificacoesAdapter
    private lateinit var rvNotificacoesN: RecyclerView
    private lateinit var rvNotificacoesLidas: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificacoesBinding.inflate(inflater, container, false)

        rvNotificacoesN = binding.rvNotificacoesNLidas
        rvNotificacoesLidas = binding.rvNotificacoesLidas


        val listaTeste =  mutableListOf(
            DataClassNotificacoes("teste1", "teste", LocalTime.of(9, 30), LocalDate.now(), false),
        DataClassNotificacoes("Ablubluble", "bleble", LocalTime.of(15, 0), LocalDate.now(), false),
        DataClassNotificacoes("SFGGDG", "ssdfsdf", LocalTime.of(12, 45), LocalDate.now().minusDays(1), false)
        )

        adapterN = NotificacoesAdapter(listaTeste)
        adapterLidas = NotificacoesAdapter(mutableListOf())

        rvNotificacoesN.layoutManager = LinearLayoutManager(requireContext())
        rvNotificacoesLidas.layoutManager = LinearLayoutManager(requireContext())

        rvNotificacoesN.adapter = adapterN
        rvNotificacoesLidas.adapter = adapterLidas

        setupTrocarSlide(rvNotificacoesN, adapterN, adapterLidas)

        ajustarAlturaRecyclerView(rvNotificacoesN, listaTeste.size)

        val btnNotificacoesLidas = binding.btnNotificacoesLidas
        btnNotificacoesLidas.isEnabled = adapterLidas.itemCount > 0

        btnNotificacoesLidas.setOnClickListener {
            TransitionManager.beginDelayedTransition(rvNotificacoesLidas, AutoTransition())
            if (rvNotificacoesLidas.isVisible) {
                rvNotificacoesLidas.visibility = View.GONE
                btnNotificacoesLidas.setImageResource(R.drawable.notificacaofechado)
            } else {
                rvNotificacoesLidas.visibility = View.VISIBLE
                btnNotificacoesLidas.setImageResource(R.drawable.notificacaoaberto)
            }

            ajustarAlturaRecyclerView(rvNotificacoesLidas, adapterLidas.itemCount)
        }

        val btnLixeira = binding.btnLixeira

        btnLixeira.setOnClickListener {
            adapterLidas.clearAll()
            ajustarAlturaRecyclerView(rvNotificacoesN, 0)
        }

        return binding.root
    }

    private fun setupTrocarSlide(
        recyclerView: RecyclerView,
        fonteAdapter: NotificacoesAdapter,
        alvoAdapter: NotificacoesAdapter
    ) {
        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = fonteAdapter.removeItemAt(position)

                item.lida = true
                alvoAdapter.addItem(item)


                binding.btnNotificacoesLidas.isEnabled = alvoAdapter.itemCount != 0

                    ajustarAlturaRecyclerView(rvNotificacoesLidas,alvoAdapter.itemCount )


            } })

        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun ajustarAlturaRecyclerView(recyclerView: RecyclerView, qtdItens: Int){
        val heighPerItemDp = 260
        val totalHeightDp = heighPerItemDp * qtdItens

        val scale = resources.displayMetrics.density
        val totalHeightPx = (totalHeightDp * scale).toInt()

        recyclerView.layoutParams?.height = totalHeightPx
        recyclerView.requestLayout()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
