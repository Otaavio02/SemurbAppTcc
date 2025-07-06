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
import androidx.recyclerview.widget.LinearLayoutManager
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.OcorrenciasAdapter
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias1Binding
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrencias2Binding
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciasBinding
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper

class OcorrenciasFragment : Fragment() {

    private var _binding: FragmentOcorrenciasBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OcorrenciasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOcorrenciasBinding.inflate(inflater, container, false)

        adapter = OcorrenciasAdapter()
        binding.rvOcorrencias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOcorrencias.adapter = adapter

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

    override fun onResume() {
        super.onResume()
        val dbHelper = ocorrenciasDBHelper(requireContext())
        val lista = dbHelper.getAllOcorrencias()
        adapter.submitList(lista)

        val heightPerItemDp = 120
        val totalHeightDp = heightPerItemDp * lista.size


        val scale = resources.displayMetrics.density
        val totalHeightPx = (totalHeightDp * scale).toInt()


        binding.rvOcorrencias.layoutParams?.height = totalHeightPx
        binding.rvOcorrencias.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
