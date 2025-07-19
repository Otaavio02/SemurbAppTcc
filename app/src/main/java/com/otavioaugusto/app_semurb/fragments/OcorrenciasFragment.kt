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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.OcorrenciasAdapter
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciasBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OcorrenciasFragment : Fragment() {

    private var _binding: FragmentOcorrenciasBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OcorrenciasAdapter

    private var id_lista: String? = null
    private var data_envio: String? = null
    private var topico: String? = null

    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    private val autenticao by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOcorrenciasBinding.inflate(inflater, container, false)

        Log.d("DEBUG", "ID OCORRENCIAS da lista antes de pegar: ${id_lista}")
        id_lista = arguments?.getString("ID_LISTA")
        data_envio = arguments?.getString("DATA_ENVIO")
        topico = arguments?.getString("TOPICO")
        Log.d("DEBUG", "ID OCORRENCIAS da lista depois de pegar: ${id_lista}")

        if (id_lista != null) {
            binding.btnEnviarOcorrencia.visibility = View.GONE
            binding.btnAdicionarOcorrencia.visibility = View.GONE
            binding.ListagemTitle.setText("${topico} - Dia ${data_envio}")
            binding.ListagemTitle.setTextColor(getResources().getColor(R.color.CorTextoPadrao))
        }


        adapter = OcorrenciasAdapter()
        binding.rvOcorrencias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOcorrencias.adapter = adapter


        binding.btnAdicionarOcorrencia.setOnClickListener {
            val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
            intent.putExtra("FRAGMENT_KEY", "INICIAR_OCORRENCIAS")
            startActivity(intent)
        }

        binding.btnVoltarOcorrencias.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                .replace(R.id.fragmentContainerView, HomeFragment())
                .addToBackStack(null)
                .commit()

            (activity as? PlaceHolderActivity)?.selecionarBottomNavBar(R.id.home)
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        atualizarListaOcorrencias(id_lista)
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun atualizarListaOcorrencias(id_lista: String?){  lifecycleScope.launch {
        val lista = withContext(Dispatchers.IO) {
            val dbHelper = AppDatabaseHelper(requireContext())
            if (id_lista == null) {
                dbHelper.getAllOcorrenciasNaoEnviadas()
            } else {
                dbHelper.getAllOcorrenciasByIdLista(id_lista)
            }
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
                val qtd_itens = listaAtual.size
                val horarioAtual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                enviarOcorrencias(qtd_itens, horarioAtual, dataAtual)
            }
        }
    }
    }

    private fun enviarOcorrencias(qtd_itens: Int, horario_envio: String, data_envio: String){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
               val dbHelper = AppDatabaseHelper(requireContext())
                val idLista = dbHelper.insertListaHistorico("Atendimento de Ocorrências", qtd_itens, horario_envio, data_envio)
                dbHelper.associarOcorrenciasALista(idLista)

                val ocorrencias = dbHelper.getAllOcorrenciasByIdLista(idLista.toString())
                val idAgenteLogado = autenticao.currentUser?.uid
                if (idAgenteLogado != null) {
                  val referenciaAgente = bancoDados.collection("agentes")
                      .document(idAgenteLogado)

                    val ocorrenciasCollection = referenciaAgente.collection("ocorrencias")


                    for (ocorrencia in ocorrencias){
                        val dados = hashMapOf(
                            "tipo" to ocorrencia.tipo,
                            "endereco" to ocorrencia.endereco,
                            "nome" to ocorrencia.nome,
                            "numcontato" to ocorrencia.numcontato,
                            "horario_envio" to horario_envio,
                            "data_envio" to data_envio

                        )

                        ocorrenciasCollection
                            .document(ocorrencia.numeroSequencial.toString())
                            .set(dados)
                            .addOnSuccessListener {
                            Log.d("FIREBASE", "Ocorrência enviada com sucesso: ${ocorrencia.numeroSequencial}")
                        }
                            .addOnFailureListener { e ->
                                Log.e("FIREBASE", "Erro ao enviar ocorrência: ${e.message}")
                            }
                        }
                    }
                }



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




