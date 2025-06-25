package com.otavioaugusto.app_semurb.fragments

import NotificacoesAdapter
import android.os.Bundle
import android.transition.AutoTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.transition.TransitionManager
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dataClasses.Notificacoes
import com.otavioaugusto.app_semurb.databinding.FragmentNotificacoesBinding
import java.time.LocalDate
import java.time.LocalTime

class NotificacoesFragment : Fragment() {

    private var _binding: FragmentNotificacoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NotificacoesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificacoesBinding.inflate(inflater, container, false)

        val listaTeste = listOf(
            Notificacoes("teste1", "teste", LocalTime.of(9, 30), LocalDate.now(), false),
            Notificacoes("Ablubluble", "bleble", LocalTime.of(15, 0), LocalDate.now(), true),
            Notificacoes("SFGGDG", "ssdfsdf", LocalTime.of(12, 45), LocalDate.now().minusDays(1), false)
        )

        adapter = NotificacoesAdapter(listaTeste)
        val rvNotificacoesN = binding.rvNotificacoesNLidas
        rvNotificacoesN.layoutManager = LinearLayoutManager(requireContext())
        rvNotificacoesN.adapter = adapter

        val btnNotificacoesLidas = binding.btnNotificacoesLidas

        btnNotificacoesLidas.setOnClickListener {
            TransitionManager.beginDelayedTransition(rvNotificacoesN, AutoTransition())
            if (rvNotificacoesN.isVisible) {
                rvNotificacoesN.visibility = View.GONE
                btnNotificacoesLidas.setImageResource(R.drawable.notificacaofechado)
            } else {
                rvNotificacoesN.visibility = View.VISIBLE
                btnNotificacoesLidas.setImageResource(R.drawable.notificacaoaberto)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
