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
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity

import com.otavioaugusto.app_semurb.R


class ViarioFragment : Fragment() {

    private lateinit var btnEnviarViario: AppCompatButton
    private lateinit var btnNovaViario: AppCompatButton
    private lateinit var btnVoltarViario: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_viario2, container, false)


        btnEnviarViario = view.findViewById(R.id.btn_enviarViario)
        btnNovaViario = view.findViewById(R.id.btn_adicionarViario)
        btnVoltarViario = view.findViewById(R.id.btn_voltarViario)


        btnEnviarViario.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "VIARIO-EDITADO")
            startActivity(intent)
        }

        btnNovaViario.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_VIARIO2")
            startActivity(intent)
        }

        btnVoltarViario.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_VIARIO")
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