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
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico1Ocorrencias
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciasBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import com.otavioaugusto.app_semurb.funcoes.VerificarHorario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

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
            val dbHelper = AppDatabaseHelper(requireContext())
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
            val listaAtual = adapter.currentList
            if (listaAtual.isEmpty()) {
                Toast.makeText(requireContext(), "Não há nenhuma ocorrência", Toast.LENGTH_SHORT).show()
            } else {
                val idsParaEnviar = listaAtual.map { it.id }
                enviarOcorrencias(idsParaEnviar)


                val horarioAtual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                val itemHistorico = DataClassHistorico1Ocorrencias(
                    id = 0,
                    numeroSequencial = 0,
                    tipo = "Ocorrência",
                    endereco = "-",
                    nome = "-",
                    numcontato = "-",
                    titulo = "Envio de ${listaAtual.size} ocorrência(s)",
                    horarioEnvio = horarioAtual,
                    dataEnvio = dataAtual,
                    quantidadeTotal = listaAtual.size
                )
            }
        }
    }
    }


    private fun enviarOcorrencias(ids: List<Int>){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
               val dbHelper = AppDatabaseHelper(requireContext())
                dbHelper.marcarOcorrenciasComoEnviadas(ids)
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


