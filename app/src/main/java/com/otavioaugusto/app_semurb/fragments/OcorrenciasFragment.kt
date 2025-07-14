package com.otavioaugusto.app_semurb.fragments
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.OcorrenciasAdapter
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciasBinding
import com.otavioaugusto.app_semurb.dbHelper.ocorrenciasDBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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


        binding.btnAdicionarOcorrencia.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_OCORRENCIAS")
            startActivity(intent)
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        atualizarListaOcorrencias()
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun atualizarListaOcorrencias(){  lifecycleScope.launch {
        val lista = withContext(Dispatchers.IO) {
            val dbHelper = ocorrenciasDBHelper(requireContext())
            dbHelper.getAllOcorrenciasNaoEnviadas()
        }

        adapter.submitList(lista)

        val heightPorItemDp = 120
        val totalHeightDp = heightPorItemDp * lista.size
        val escala = resources.displayMetrics.density
        val totalHeightPx = (totalHeightDp * escala).toInt()
        binding.rvOcorrencias.layoutParams?.height = totalHeightPx
        binding.rvOcorrencias.requestLayout()


        binding.btnEnviarOcorrencia.setOnClickListener {
            if (lista.isEmpty()) {
                Toast.makeText(requireContext(), "Não há nenhuma ocorrência", Toast.LENGTH_SHORT).show()
            } else {
                enviarOcorrencias()
            }
        }
    }
    }


    private fun enviarOcorrencias(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                ocorrenciasDBHelper(requireContext()).marcarOcorrenciasComoEnviadas()
            }

            adapter.submitList(emptyList())
            Toast.makeText(requireContext(), "Ocorrências Enviadas", Toast.LENGTH_SHORT).show()
            (activity as? PlaceHolderActivity)?.selecionarBottomNavBar(R.id.home)

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                )
                .replace(R.id.fragmentContainerView, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}


