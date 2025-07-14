package com.otavioaugusto.app_semurb.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.adapters.ViarioAdapter
import com.otavioaugusto.app_semurb.databinding.FragmentViarioBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViarioFragment : Fragment() {

    private var _binding: FragmentViarioBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ViarioAdapter

    companion object {
        private const val FRAGMENT_KEY = "FRAGMENT_KEY"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViarioBinding.inflate(inflater, container, false)

        adapter = ViarioAdapter()
        binding.rvOcorrencias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOcorrencias.adapter = adapter

        binding.btnAdicionarViario.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_VIARIO")
            startActivity(intent)
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

        lifecycleScope.launch {

            val lista =  withContext(Dispatchers.IO){
                val dbHelper = AppDatabaseHelper(requireContext())
                dbHelper.getAllViariosNaoEnviados()
            }
                adapter.submitList(lista)
            val heightPorItemDp = 120
            val totalHeightDp = heightPorItemDp * lista.size

            val escala = resources.displayMetrics.density
            val totalHeightPx = (totalHeightDp * escala).toInt()

            binding.rvOcorrencias.layoutParams?.height = totalHeightPx
            binding.rvOcorrencias.requestLayout()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
