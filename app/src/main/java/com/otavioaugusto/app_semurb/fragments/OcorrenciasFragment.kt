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
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias1Binding
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias2Binding
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciasBinding

class OcorrenciasFragment : Fragment() {

    private var _binding: FragmentOcorrenciasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOcorrenciasBinding.inflate(inflater, container, false)

        binding.btnEnviarOcorrencia.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "OCORRENCIAS_EDITADO")
            intent.putExtra("VISIBILITY","GONE")
            startActivity(intent)
        }

        binding.btnAdicionarOcorrencia.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_OCORRENCIAS")
            startActivity(intent)
        }

        binding.btnVoltarOcorrencia.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
