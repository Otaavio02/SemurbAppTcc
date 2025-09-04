package com.otavioaugusto.app_semurb.fragments
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.firestore.dataObjects
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.OcorrenciasAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico1Ocorrencias
import com.otavioaugusto.app_semurb.dataClasses.DataClassOcorrencia
import com.otavioaugusto.app_semurb.databinding.FragmentOcorrenciasBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import com.otavioaugusto.app_semurb.funcoes.EnviarNotificacaoBd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.firestore.Query

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

        Log.d("DEBUG", "ID OCORRENCIAS da lista antes de pegar: ${data_envio}")
        data_envio = arguments?.getString("DATA_ENVIO")
        topico = arguments?.getString("TOPICO")
        Log.d("DEBUG", "ID OCORRENCIAS da lista depois de pegar: ${data_envio}")

        if (data_envio != null) {
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
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainerView, HomeFragment())
                .commit()

            (activity as? PlaceHolderActivity)?.selecionarBottomNavBar(R.id.home)
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        atualizarListaOcorrencias(data_envio)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(R.id.fragmentContainerView, HomeFragment())
                    .commit()

                (activity as? PlaceHolderActivity)?.selecionarBottomNavBar(R.id.home)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun atualizarListaOcorrencias(data_envio: String?){  lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            val idAgenteLogado = autenticao.currentUser?.uid
            val dbHelper = AppDatabaseHelper(requireContext())

            if (data_envio == null) {
                val lista = dbHelper.getAllOcorrenciasNaoEnviadas()

                lifecycleScope.launch(Dispatchers.Main) {
                    adapter.submitList(lista)

                    val heightPorItemDp = 120
                    val totalHeightDp = heightPorItemDp * lista.size
                    val escala = resources.displayMetrics.density
                    val totalHeightPx = (totalHeightDp * escala).toInt()
                    binding.rvOcorrencias.layoutParams?.height = totalHeightPx
                    binding.rvOcorrencias.requestLayout()
                }
            } else {

                val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatoSaida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val dataConvertida = try {
                    val data = formatoEntrada.parse(data_envio)
                    formatoSaida.format(data!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

                Log.d("DEBUG", "data convertida: ${dataConvertida}")

                bancoDados.collection("agentes")
                    .document(idAgenteLogado.toString()).collection("ocorrencias").document(dataConvertida.toString()).collection("lista")
                    .orderBy("horario_envio", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { ocorrencias ->
                        val listaConvertida = ocorrencias.mapNotNull { doc ->
                            val dados = doc.data

                            try {
                                val endereco = dados["endereco"] as String
                                val numcontato = dados["numcontato"] as String
                                val tipo = dados["tipo"] as String
                                val nome = dados["nome"] as String
                                val id = doc.id

                                DataClassOcorrencia(
                                    id = id.toInt(),
                                    numeroSequencial = id.toInt(),
                                    tipo = tipo,
                                    endereco = endereco,
                                    nome = nome,
                                    numcontato = numcontato,
                                    data_envio = data_envio
                                )
                            } catch (e: Exception) {
                                Log.e("FIREBASE", "Erro ao converter item da lista", e)
                                null
                            }
                        }

                        // Atualizando adapter na UI thread
                        lifecycleScope.launch(Dispatchers.Main) {
                            adapter.submitList(listaConvertida)

                            val heightPorItemDp = 120
                            val totalHeightDp = heightPorItemDp * listaConvertida.size
                            val escala = resources.displayMetrics.density
                            val totalHeightPx = (totalHeightDp * escala).toInt()
                            binding.rvOcorrencias.layoutParams?.height = totalHeightPx
                            binding.rvOcorrencias.requestLayout()
                        }
                    }
                    .addOnFailureListener {
                        Log.e("FIREBASE", "Erro ao buscar ocorrências do histórico", it)
                    }
            }
        }




        binding.btnEnviarOcorrencia.setOnClickListener {
            val listaAtual = adapter.currentList
            if (listaAtual.isEmpty()) {
                Toast.makeText(requireContext(), "Não há nenhuma ocorrência", Toast.LENGTH_SHORT).show()
            } else {
                val horarioAtual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val qtd_itens = listaAtual.size

                enviarOcorrencias(qtd_itens, horarioAtual, dataAtual)

                EnviarNotificacaoBd().notificacaoOcorrencia("Notificação de ocorrência", "Ocorrências enviadas com sucesso", dataAtual, horarioAtual, )
            }
        }
    }
    }

    private fun enviarOcorrencias(qtd_itens: Int, horario_envio: String, data_envio: String){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
               val dbHelper = AppDatabaseHelper(requireContext())
                val idLista = dbHelper.insertListaHistorico("Atendimento de Ocorrências", qtd_itens, horario_envio, data_envio)
                dbHelper.associarOcorrenciasALista(idLista, data_envio)

                val ocorrencias = dbHelper.getAllOcorrenciasByIdLista(idLista.toString())

                Log.d("debug", "TESTE TESTE: ${ocorrencias}")

                val idAgenteLogado = autenticao.currentUser?.uid
                if (idAgenteLogado != null) {
                  val referenciaAgente = bancoDados.collection("agentes")
                      .document(idAgenteLogado)



                    val dbData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val timestampLista = com.google.firebase.Timestamp.now()
                    val ocorrenciasCollection = referenciaAgente.collection("ocorrencias")
                    val ocorrenciaAtual = ocorrenciasCollection.document(dbData)
                    ocorrenciaAtual.get().addOnSuccessListener{ docSnapshot ->
                         val fieldQtd = (docSnapshot.getLong("qtd_itens") ?: 0L).toInt()

                        val dadosLista = hashMapOf(
                            "topico" to "Atendimento de Ocorrências",
                            "qtd_itens" to qtd_itens + fieldQtd,
                            "horario_envio" to horario_envio,
                            "data_envio" to data_envio,
                            "timestamp" to timestampLista
                        )

                        ocorrenciasCollection
                            .document(dbData).set(dadosLista)
                    }

                    for (ocorrencia in ocorrencias){

                        val timestampOcorrencia = com.google.firebase.Timestamp.now()

                        val dados = hashMapOf(
                            "tipo" to ocorrencia.tipo,
                            "endereco" to ocorrencia.endereco,
                            "nome" to ocorrencia.nome,
                            "numcontato" to ocorrencia.numcontato,
                            "horario_envio" to horario_envio,
                            "data_envio" to data_envio,
                            "timestamp_ocorrencia" to  timestampOcorrencia
                        )

                        ocorrenciasCollection
                            .document(dbData).collection("lista").document(ocorrencia.numeroSequencial.toString())
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
                    R.anim.fade_in,
                    R.anim.fade_out,
                )
                .replace(R.id.fragmentContainerView, HomeFragment())
                .commit()
        }


}




