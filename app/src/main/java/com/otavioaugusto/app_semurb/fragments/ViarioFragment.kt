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
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.databinding.FragmentViario2Binding
import com.otavioaugusto.app_semurb.databinding.FragmentViarioBinding

class ViarioFragment : Fragment() {

    private var _binding: FragmentViarioBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val FRAGMENT_KEY = "FRAGMENT_KEY"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViarioBinding.inflate(inflater, container, false)

        binding.btnEnviarViario.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "VIARIO_EDITADO")
            intent.putExtra("VISIBILITY","GONE")
            startActivity(intent)
        }

        binding.btnAdicionarViario.setOnClickListener {
            navigateToFragment("INICIAR_VIARIO")
        }

        binding.btnVoltarViario.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainerView, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun navigateToFragment(fragmentKey: String) {
        val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
        intent.putExtra(FRAGMENT_KEY, fragmentKey)
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
