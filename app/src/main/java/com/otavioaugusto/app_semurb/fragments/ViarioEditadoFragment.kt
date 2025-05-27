package com.otavioaugusto.app_semurb.fragments

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
import com.otavioaugusto.app_semurb.R

class ViarioEditadoFragment : Fragment() {

    private lateinit var btnvoltarViarioEditado: ImageButton
    private lateinit var btnfinalizarViarioEdicao: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_viarioeditado,
            container,
            false
        )

        btnvoltarViarioEditado = view.findViewById(R.id.btn_voltarViarioEditado)
        btnvoltarViarioEditado.setOnClickListener {

            requireActivity().finish()
        }
        btnfinalizarViarioEdicao = view.findViewById(R.id.btn_finalizarViarioEdicao)
        btnfinalizarViarioEdicao.setOnClickListener {
            requireActivity().finish()
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
        }}}