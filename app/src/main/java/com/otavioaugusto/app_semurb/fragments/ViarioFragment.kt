package com.otavioaugusto.app_semurb.fragments
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.otavioaugusto.app_semurb.adapters.ViarioAdapter
import com.otavioaugusto.app_semurb.databinding.FragmentViarioBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViarioFragment : Fragment() {

    private var _binding: FragmentViarioBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ViarioAdapter

    private var id_lista: String? = null
    private var data_envio: String? = null
    private var topico: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViarioBinding.inflate(inflater, container, false)

        Log.d("DEBUG", "ID VIARIO da lista antes de pegar: ${id_lista}")
        id_lista = arguments?.getString("ID_LISTA")
        data_envio = arguments?.getString("DATA_ENVIO")
        topico = arguments?.getString("TOPICO")
        Log.d("DEBUG", "ID VIARIO da lista depois de pegar: ${id_lista}")

        if (id_lista != null) {
            binding.btnEnviarViario.visibility = View.GONE
            binding.btnAdicionarViario.visibility = View.GONE
            binding.ListagemTitle.setText("${topico} - Dia ${data_envio}")
            binding.ListagemTitle.setTextColor(getResources().getColor(R.color.CorTextoPadrao))
        }


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


    override fun onResume() {
        super.onResume()

        atualizarListaViario(id_lista)

    }

    private fun atualizarListaViario(id_lista: String?){  lifecycleScope.launch {
        val lista = withContext(Dispatchers.IO) {
            val dbHelper = AppDatabaseHelper(requireContext())
            if (id_lista == null) {
                dbHelper.getAllViariosNaoEnviados()
            } else {
                dbHelper.getAllViariosByIdLista(id_lista)
            }
        }

        adapter.submitList(lista)

        val heightPorItemDp = 120
        val totalHeightDp = heightPorItemDp * lista.size
        val escala = resources.displayMetrics.density
        val totalHeightPx = (totalHeightDp * escala).toInt()
        binding.rvOcorrencias.layoutParams?.height = totalHeightPx
        binding.rvOcorrencias.requestLayout()


        binding.btnEnviarViario.setOnClickListener {
            val listaAtual = adapter.currentList
            if (listaAtual.isEmpty()) {
                Toast.makeText(requireContext(), "Não há nenhuma ocorrência", Toast.LENGTH_SHORT).show()
            } else {
                val qtd_itens = listaAtual.size
                val horarioAtual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                enviarViario(qtd_itens, horarioAtual, dataAtual)
            }
        }
    }
    }

    private fun enviarViario(qtd_itens: Int, horario_envio: String, data_envio: String){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val dbHelper = AppDatabaseHelper(requireContext())
                val idLista = dbHelper.insertListaHistorico("Serviço Viário", qtd_itens, horario_envio, data_envio)
                dbHelper.associarViarioALista(idLista)
            }

            adapter.submitList(emptyList())
            Toast.makeText(requireContext(), "Sinalizações Enviadas", Toast.LENGTH_SHORT).show()


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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
