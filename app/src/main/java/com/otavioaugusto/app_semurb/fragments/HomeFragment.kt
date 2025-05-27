package com.otavioaugusto.app_semurb.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.*

class HomeFragment : Fragment() {

    private lateinit var btnHistorico: ImageButton
    private lateinit var btnViario: ImageButton
    private lateinit var btnInspecao: ImageButton
    private lateinit var btnOcorrencia: ImageButton
    private lateinit var btnNotificacao: ImageButton
    private lateinit var btnIniciarTurno: AppCompatButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        btnNotificacao = view.findViewById(R.id.btn_notifcacao_home)
        btnViario = view.findViewById(R.id.btn_viario_home)
        btnInspecao = view.findViewById(R.id.btn_inspecao_home)
        btnHistorico = view.findViewById(R.id.btn_historico_home)
        btnOcorrencia = view.findViewById(R.id.btn_ocorrencia_home)
        btnIniciarTurno = view.findViewById(R.id.btn_iniciarTurno)


        btnNotificacao.setOnClickListener {


            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                )
                .replace(R.id.fragmentContainerView, NotificacoesFragment())
                .addToBackStack(null)
                .commit()
        }

        btnViario.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_VIARIO")
            startActivity(intent)
        }

        btnInspecao.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_INSPECAO")
            startActivity(intent)
        }

        btnHistorico.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                )
                .replace(R.id.fragmentContainerView, Historico1Fragment())
                .addToBackStack(null)
                .commit()
        }

        btnOcorrencia.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_OCORRENCIAS")
            startActivity(intent)
        }

        btnIniciarTurno.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_TURNO")
            startActivity(intent)
        }

        return view
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
}