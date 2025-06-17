package com.otavioaugusto.app_semurb.fragments

import NotificacoesAdapter
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.dataClasses.Notificacoes
import com.otavioaugusto.app_semurb.databinding.FragmentNotificacoesBinding
import java.time.LocalDate
import java.time.LocalTime
import android.transition.TransitionManager

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
            Notificacoes("Yahuha", "O rui vai te pegar", LocalTime.of(9, 30), LocalDate.now(), false),
            Notificacoes("Ablubluble", "Brr brr Patapim", LocalTime.of(15, 0), LocalDate.now(), true),
            Notificacoes("SFGGDG", "ssdfsdf", LocalTime.of(12, 45), LocalDate.now().minusDays(1), false)
        )


        adapter = NotificacoesAdapter(listaTeste)
        val rvNotificacoesN = binding.rvNotificacoesNLidas
        rvNotificacoesN.layoutManager = LinearLayoutManager(requireContext())
        rvNotificacoesN.adapter = adapter

        val btnNotificacoesLidas = binding.btnNotificacoesLidas


        btnNotificacoesLidas.setOnClickListener {
            btnNotificacoesLidas.setImageResource(R.drawable.notificacoesfechado)
            TransitionManager.beginDelayedTransition(rvNotificacoesN, AutoTransition())
            if (rvNotificacoesN.isVisible) {
                rvNotificacoesN.visibility = View.GONE

                btnNotificacoesLidas.setImageResource(R.drawable.notificacoesfechado)

            } else {
                rvNotificacoesN.visibility = View.VISIBLE
                btnNotificacoesLidas.setImageResource(R.drawable.notificacaoaberto)
            }
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let {
                    it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
