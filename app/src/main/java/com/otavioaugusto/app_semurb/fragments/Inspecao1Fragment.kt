package com.otavioaugusto.app_semurb.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment

import com.otavioaugusto.app_semurb.R



class Inspecao1Fragment : Fragment() {





    private lateinit var btnvoltarInspecao: ImageButton
    private lateinit var btnproximoInspecao: AppCompatButton
    private lateinit var checkBoxSemAvaria: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inspecao, container, false)
        checkBoxSemAvaria = view.findViewById(R.id.checkBox_SemAvaria)
        btnproximoInspecao = view.findViewById(R.id.btn_proximoInspecao1)
        btnvoltarInspecao = view.findViewById(R.id.btn_voltarInspecao)
        btnvoltarInspecao.setOnClickListener {
            requireActivity().finish()
        }
        btnproximoInspecao.setOnClickListener {
            if (checkBoxSemAvaria.isChecked){
                requireActivity().finish()
            } else {


                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                    )
                    .replace(R.id.FragmentContainerView2, Inspecao2Fragment())
                    .addToBackStack(null)
                    .commit()
            }
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