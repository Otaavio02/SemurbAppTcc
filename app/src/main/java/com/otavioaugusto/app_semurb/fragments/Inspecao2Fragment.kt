package com.otavioaugusto.app_semurb.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity

import com.otavioaugusto.app_semurb.R

class Inspecao2Fragment : Fragment() {

    private lateinit var btnvoltarInspecao2: ImageButton
    private lateinit var btnproximoInspecao2: AppCompatButton
    private var etapaAtual = 1
    private var totalEtapas = 3



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inspecao2, container, false)






        btnvoltarInspecao2 = view.findViewById(R.id.btn_voltarInspecao2)
        btnvoltarInspecao2.setOnClickListener {

            if (etapaAtual > 0) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual - 1)
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.FragmentContainerView2, Inspecao1Fragment())
                .addToBackStack(null)
                .commit()
        }

        btnproximoInspecao2 = view.findViewById(R.id.btn_proximoInspecao2)
        btnproximoInspecao2.setOnClickListener {

            if (etapaAtual < totalEtapas - 1) {
                (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual + 1)
            }

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,

                )
                .replace(R.id.FragmentContainerView2, Inspecao3Fragment())
                .addToBackStack(null)
                .commit()
        }




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            (activity as? PlaceHolderGameficadoActivity)?.moverCarrinhoParaEtapa(etapaAtual)
        }
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



