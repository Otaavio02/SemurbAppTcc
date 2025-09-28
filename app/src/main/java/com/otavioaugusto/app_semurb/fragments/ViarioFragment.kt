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
import com.google.firebase.firestore.Query
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.ViarioAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassOcorrencia
import com.otavioaugusto.app_semurb.dataClasses.DataClassViario
import com.otavioaugusto.app_semurb.databinding.FragmentViarioBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import com.otavioaugusto.app_semurb.funcoes.EnviarNotificacaoBd
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
        _binding = FragmentViarioBinding.inflate(inflater, container, false)

        Log.d("DEBUG", "ID VIARIO da lista antes de pegar: ${id_lista}")
        id_lista = arguments?.getString("ID_LISTA")
        data_envio = arguments?.getString("DATA_ENVIO")
        topico = arguments?.getString("TOPICO")
        Log.d("DEBUG", "ID VIARIO da lista depois de pegar: ${id_lista}")

        if (data_envio != null) {
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


            binding.btnVoltarViario.setOnClickListener {
                if (data_envio == null) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.fragmentContainerView, HomeFragment())
                        .commit()

                    (activity as? PlaceHolderActivity)?.selecionarBottomNavBar(R.id.home)
                } else {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.fragmentContainerView, HistoricoFragment())
                        .commit()

                    (activity as? PlaceHolderActivity)?.limparBottomNavBar()
                }
            }

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        atualizarListaViario(data_envio)

    }

    private fun atualizarListaViario(data_envio: String?) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val idAgenteLogado = autenticao.currentUser?.uid
                val dbHelper = AppDatabaseHelper(requireContext())

                if (data_envio == null) {
                    val lista = dbHelper.getAllViariosNaoEnviados()

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

                    bancoDados.collection("agentes")
                        .document(idAgenteLogado.toString()).collection("viario")
                        .document(dataConvertida.toString()).collection("lista")
                        .orderBy("horario_envio", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { viarios ->
                            val listaConvertida = viarios.mapNotNull { doc ->
                                val dados = doc.data

                                try {
                                    val endereco = dados["endereco"] as String
                                    val tipo = dados["tipo"] as String
                                    val descricao = dados["descricao"] as String
                                    val fotoUrl = dados["fotoUrl"] as String?
                                    val numero_sequencial = dados["numero_sequencial"]
                                    val id_item = numero_sequencial.toString()

                                    DataClassViario(
                                        id = id_item.toLong(),
                                        numeroSequencial = numero_sequencial,
                                        tipo = tipo,
                                        endereco = endereco,
                                        descricao = descricao,
                                        data_envio = data_envio,
                                        foto_url = fotoUrl
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

                binding.btnEnviarViario.setOnClickListener {
                    val listaAtual = adapter.currentList
                    if (listaAtual.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Não há nenhuma ocorrência",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val qtd_itens = listaAtual.size
                        val horarioAtual =
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        val dataAtual =
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                        enviarViario(qtd_itens, horarioAtual, dataAtual)

                        EnviarNotificacaoBd().notificacaoOcorrencia(
                            "Notificação de Viário",
                            "Viário adicionado com sucesso.",
                            dataAtual,
                            horarioAtual
                        )

                    }
                }
            }
        }
    }

    private fun enviarViario(qtd_itens: Int, horario_envio: String, data_envio: String){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val dbHelper = AppDatabaseHelper(requireContext())
                val idLista = dbHelper.insertListaHistorico("Serviço Viário", qtd_itens, horario_envio, data_envio)
                dbHelper.associarViarioALista(idLista, data_envio)

                val viarios = dbHelper.getAllViariosByIdLista(idLista.toString())

                val idAgenteLogado = autenticao.currentUser?.uid
                if (idAgenteLogado != null) {
                    val referenciaAgente = bancoDados.collection("agentes")
                        .document(idAgenteLogado)

                    val dbData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val timestampLista = com.google.firebase.Timestamp.now()
                    val viariosCollection = referenciaAgente.collection("viario")
                    val viarioAtual = viariosCollection.document(dbData)
                    viarioAtual.get().addOnSuccessListener { docSnapshot ->
                        val fieldQtd = (docSnapshot.getLong("qtd_itens") ?: 0L).toInt()

                        val dadosLista = hashMapOf(
                            "topico" to "Serviço Viário",
                            "qtd_itens" to qtd_itens + fieldQtd,
                            "horario_envio" to horario_envio,
                            "data_envio" to data_envio,
                            "timestamp" to timestampLista
                        )

                        viariosCollection
                            .document(dbData).set(dadosLista)
                    }

                    for (viario in viarios){

                        val timestampViario = com.google.firebase.Timestamp.now()

                        val dados = hashMapOf(
                            "numero_sequencial" to viario.numeroSequencial,
                            "tipo" to viario.tipo,
                            "endereco" to viario.endereco,
                            "descricao" to viario.descricao,
                            "fotoUrl" to viario.foto_url,
                            "horario_envio" to horario_envio,
                            "data_envio" to data_envio,
                            "timestamp_viario" to  timestampViario
                        )

                        viariosCollection
                            .document(dbData).collection("lista").document("${horario_envio}h - " + viario.numeroSequencial.toString())
                            .set(dados)
                            .addOnSuccessListener {
                                Log.d("FIREBASE", "Ocorrência enviada com sucesso: ${viario.numeroSequencial}")
                                dbHelper.resetTable("viario")
                            }
                            .addOnFailureListener { e ->
                                Log.e("FIREBASE", "Erro ao enviar ocorrência: ${e.message}")
                            }
                    }
                }
            }

            adapter.submitList(emptyList())
            Toast.makeText(requireContext(), "Sinalizações Enviadas", Toast.LENGTH_SHORT).show()


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (data_envio == null) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.fragmentContainerView, HomeFragment())
                        .commit()

                    (activity as? PlaceHolderActivity)?.selecionarBottomNavBar(R.id.home)
                } else {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.fragmentContainerView, HistoricoFragment())
                        .commit()

                    (activity as? PlaceHolderActivity)?.limparBottomNavBar()
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
