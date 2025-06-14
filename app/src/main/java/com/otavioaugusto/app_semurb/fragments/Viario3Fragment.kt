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
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.R

class Viario3Fragment: Fragment() {

    private lateinit var btnVoltarViario3: ImageButton
    private lateinit var btnProximoViario3: AppCompatButton
    private lateinit var carrinhoMovimento: ImageView
    private lateinit var bolasViario: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_viario3,
            container,
            false
        )


        btnVoltarViario3 = view.findViewById(R.id.btn_voltarViario3)
        btnVoltarViario3.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderActivity::class.java)
            intent.putExtra("FRAGMENT_KEY2", "INICIAR_VIARIOHOME")
            startActivity(intent)
        }







        btnProximoViario3 = view.findViewById(R.id.btn_proximoViario3)
        btnProximoViario3.setOnClickListener {


            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,

                )
                .replace(R.id.FragmentContainerView2, Viario4Fragment())
                .addToBackStack(null)
                .commit()
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