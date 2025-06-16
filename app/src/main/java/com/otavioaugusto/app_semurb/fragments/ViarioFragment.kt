package com.otavioaugusto.app_semurb.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.fragment.app.Fragment
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.databinding.FragmentViario2Binding

class ViarioFragment : Fragment() {

    private var _binding: FragmentViario2Binding? = null
    private val binding get() = _binding!!

    companion object {
        private const val FRAGMENT_KEY = "FRAGMENT_KEY"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViario2Binding.inflate(inflater, container, false)

        binding.btnEnviarViario.setOnClickListener {
            navigateToFragment("VIARIO-EDITADO")
        }

        binding.btnAdicionarViario.setOnClickListener {
            navigateToFragment("INICIAR_VIARIO2")
        }

        binding.btnVoltarViario.setOnClickListener {
            navigateToFragment("INICIAR_VIARIO")
        }

        return binding.root
    }

    private fun navigateToFragment(fragmentKey: String) {
        val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
        intent.putExtra(FRAGMENT_KEY, fragmentKey)
        startActivity(intent)
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
