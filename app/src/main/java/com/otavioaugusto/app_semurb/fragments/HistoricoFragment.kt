package com.otavioaugusto.app_semurb.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.otavioaugusto.app_semurb.PlaceHolderActivity
import com.otavioaugusto.app_semurb.PlaceHolderGameficadoActivity

import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.HistoricoAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistoricoListItem
import com.otavioaugusto.app_semurb.databinding.FragmentHistoricoBinding
import com.otavioaugusto.app_semurb.dbHelper.AppDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.mutableListOf

class HistoricoFragment : Fragment() {

    private var _binding: FragmentHistoricoBinding? = null
    private val binding get() = _binding!!

    private var categoriaSelecionada = "Tudo"
    private var dataSelecionada: String? = null
    private var dataSelecionadaBD: String? = null

    private lateinit var adapter: HistoricoAdapter

    val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    private val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }

    lateinit private var usuarioID: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricoBinding.inflate(inflater, container, false)

        val historicoEspecifico = resources.getStringArray(R.array.historico)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, historicoEspecifico)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        val editTextDate = binding.editTextData
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            dataSelecionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            dataSelecionadaBD = String.format("%02d-%02d-%d", year, month + 1, dayOfMonth,)
            editTextDate.setText(dataSelecionada)
            binding.iconErase.visibility = View.VISIBLE;

            if (categoriaSelecionada == "Tudo") { atualizarHistorico() } else { atualizarHistoricoPorCategoria()}
        }
        editTextDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.autoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
            categoriaSelecionada = adapterView.getItemAtPosition(i).toString()

            if (categoriaSelecionada == "Tudo") { atualizarHistorico() } else { atualizarHistoricoPorCategoria()}
        }

        binding.iconErase.setOnClickListener {
            editTextDate.setText("Pesquisar data...")
            dataSelecionada = null
            dataSelecionadaBD = null
            binding.iconErase.visibility = View.GONE
            if (categoriaSelecionada == "Tudo") { atualizarHistorico() } else { atualizarHistoricoPorCategoria()}
        }

        Log.d("DEBUG", "DATA E CATEGORIA: ${dataSelecionada}, ${categoriaSelecionada}")

        binding.btnVoltarHistorico.setOnClickListener {
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

        atualizarHistorico()
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

    private fun atualizarHistorico() {
        val idUsuarioLogado = autenticacao.currentUser?.uid ?: return
        usuarioID = idUsuarioLogado
        val listaFinal = mutableListOf<Pair<String, Map<String, Any>>>()

        val categorias = listOf("viario", "ocorrencias", "inspecoes")
        val totalCategorias = categorias.size
        var respostasRecebidas = 0

        for (categoria in categorias) {
            if (categoria == "inspecoes") {
                val colecao = bancoDados.collection("inspecoes").whereEqualTo("motoristaID", usuarioID)
                val query = if (dataSelecionada == null) {
                    colecao.orderBy("dataRegistro", Query.Direction.DESCENDING)
                } else {
                    colecao.whereEqualTo("dataRegistro", dataSelecionada)
                }

                query.get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            listaFinal.add(Pair("inspecoes", doc.data))

                        }
                        Log.d("testes", "LISTA FINAL: ${listaFinal}")
                        verificarSeTerminou(++respostasRecebidas, totalCategorias, listaFinal)
                    }
                    .addOnFailureListener {
                        Log.e("FIREBASE", "Erro ao puxar inspeções", it)
                        verificarSeTerminou(++respostasRecebidas, totalCategorias, listaFinal)
                    }

            } else {
                val colecao = bancoDados.collection("agentes").document(idUsuarioLogado).collection(categoria)
                val query = if (dataSelecionada != null) {
                    colecao.whereEqualTo("data_envio", dataSelecionada)
                } else {
                    colecao.orderBy("timestamp", Query.Direction.DESCENDING)
                }

                query.get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            listaFinal.add(Pair(categoria, doc.data))
                        }
                        verificarSeTerminou(++respostasRecebidas, totalCategorias, listaFinal)
                    }
                    .addOnFailureListener {
                        Log.e("FIREBASE", "Erro ao puxar $categoria", it)
                        verificarSeTerminou(++respostasRecebidas, totalCategorias, listaFinal)
                    }
            }
        }
    }

    private fun verificarSeTerminou(
        recebidas: Int,
        total: Int,
        listaFinal: List<Pair<String, Map<String, Any>>>
    ) {
        if (recebidas == total) {
            Log.d("FIREBASE", "Todas as categorias carregadas: ${listaFinal.size} itens")

            // Transforma os dados recebidos em objetos DataClassHistorico
            val dadosBrutos = listaFinal.mapNotNull { (categoria, dados) ->
                try {

                    val dataEnvio = when (categoria) {
                        "inspecoes" -> {
                            val ts = dados["dataRegistro"] as? com.google.firebase.Timestamp
                            ts?.toDate()?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) }
                        }
                        else -> dados["data_envio"] as? String
                    } ?: return@mapNotNull null

                    val horarioEnvio = when (categoria) {
                        "inspecoes" -> {
                            val ts = dados["dataRegistro"] as? com.google.firebase.Timestamp
                            ts?.toDate()?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(it) }
                        }
                        else -> dados["horario_envio"] as? String
                    } ?: "--:--"

                    val viaturaID = when (categoria) {
                        "inspecoes" -> { dados["viaturaID"] as? String }
                        else -> dados["viatura_usada"] as? String
                    }

                    val qtdItens = (dados["qtd_itens"] as? Long)?.toInt() ?: 1

                    val topico = when (categoria) {
                        "viario" -> "Serviço Viário"
                        "ocorrencias" -> "Atendimento de Ocorrências"
                        "inspecoes" -> "Inspeção da Viatura"
                        else -> "Outro"
                    }

                    DataClassHistorico(
                        qtd_itens = qtdItens,
                        data_envio = dataEnvio,
                        horario_envio = horarioEnvio,
                        topico = topico,
                        viatura_usada = viaturaID
                    )
                } catch (e: Exception) {
                    Log.e("FIREBASE", "Erro ao converter dados para DataClassHistorico", e)
                    null
                }
            }

            val dadosOrdenados = dadosBrutos.sortedWith(compareByDescending<DataClassHistorico> {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.data_envio)
            }.thenByDescending {
                SimpleDateFormat("HH:mm", Locale.getDefault()).parse(it.horario_envio)
            })

            val agrupado = dadosOrdenados.groupBy { it.data_envio }

            val listaFinalAdapter = mutableListOf<DataClassHistoricoListItem>()
            agrupado.forEach { (data, itens) ->
                listaFinalAdapter.add(DataClassHistoricoListItem.Header(data))
                itens.forEach { item ->
                    listaFinalAdapter.add(DataClassHistoricoListItem.Item(item))
                }
            }

            // Atualiza a RecyclerView
            adapter = HistoricoAdapter(listaFinalAdapter) { historico ->
                val fragment = when (historico.topico) {
                    "Atendimento de Ocorrências" -> OcorrenciasFragment()
                    "Serviço Viário" -> ViarioFragment()
                    "Inspeção da Viatura" -> Inspecao3Fragment()
                    else -> HomeFragment()
                }

                val bundle = Bundle().apply {
                    putString("DATA_ENVIO", historico.data_envio)
                    putString("viaturaID", historico.viatura_usada)
                    putString("usuarioID", usuarioID)
                    putString("TOPICO", historico.topico)
                }
                fragment.arguments = bundle

                Log.d("TEST", "VIAUTRA ID Q ERA PRA TA APARECENDO: ${historico.viatura_usada}")

                val intent = Intent(requireContext(), PlaceHolderGameficadoActivity::class.java)
                intent.putExtra("FRAGMENT_KEY", "HISTORICO_INSPECAO")
                intent.putExtra("DATA_ENVIO", historico.data_envio)
                intent.putExtra("viaturaID", historico.viatura_usada)
                intent.putExtra("usuarioID", usuarioID)
                intent.putExtra("TOPICO", historico.topico)

                if (historico.topico == "Inspeção da Viatura"){
                    startActivity(intent)
                } else {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        .replace(R.id.fragmentContainerView, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

            binding.rvHistorico.adapter = adapter
            binding.rvHistorico.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun atualizarHistoricoPorCategoria() {
        val idUsuarioLogado = autenticacao.currentUser?.uid
        val listaFinal = mutableListOf<Pair<String, Map<String, Any>>>()

        val totalCategorias = 1
        var respostasRecebidas = 0

        val categoria = when (categoriaSelecionada) {
            "Serviço Viário" -> "viario"
            "Ocorrências" -> "ocorrencias"
            "Inspeção" -> "inspecoes"
            else -> return
        }

        if (dataSelecionada == null){
            if (idUsuarioLogado != null){
                if (categoria == "inspecoes"){
                    bancoDados.collection("inspecoes").whereEqualTo("motoristaID", usuarioID)
                        .orderBy("dataRegistro")
                        .get().addOnSuccessListener { docs ->
                            for (doc in docs) {
                                listaFinal.add(Pair(categoria, doc.data))
                                Log.d("FIREBASE", "[$categoria] ${doc.id} => ${doc.data}")
                            }
                            verificarSeTerminou(++respostasRecebidas, totalCategorias, listaFinal)
                        }.addOnFailureListener {
                            Log.e("FIREBASE", "Erro ao puxar $categoria", it)
                        }
                } else {
                    bancoDados.collection("agentes").document(idUsuarioLogado).collection(categoria)
                        .orderBy("data_envio", Query.Direction.DESCENDING)
                        .get().addOnSuccessListener { docs ->
                            for (doc in docs) {
                                listaFinal.add(Pair(categoria, doc.data))
                                Log.d("FIREBASE", "[$categoria] ${doc.id} => ${doc.data}")
                            }
                            verificarSeTerminou(++respostasRecebidas, totalCategorias, listaFinal)
                        }.addOnFailureListener {
                            Log.e("FIREBASE", "Erro ao puxar $categoria", it)
                        }
                }
            }
        } else {
            if (idUsuarioLogado != null){
                if (categoria == "inspecoes"){
                    bancoDados.collection("inspecoes")
                        .whereEqualTo("motoristaID", usuarioID)
                        .whereEqualTo("data_envio", dataSelecionada)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (doc in querySnapshot.documents) {
                                listaFinal.add(Pair(categoria, doc.data ?: emptyMap()))
                                Log.d("FIREBASE", "[$categoria] ${doc.id} => ${doc.data}")
                            }
                            verificarSeTerminou(++respostasRecebidas, totalCategorias, listaFinal)
                        }
                        .addOnFailureListener {
                            Log.e("FIREBASE", "Erro ao puxar $categoria", it)
                        }
                } else {
                    bancoDados.collection("agentes").document(idUsuarioLogado).collection(categoria)
                        .whereEqualTo("data_envio", dataSelecionada)
                        .get().addOnSuccessListener { docs ->
                            for (doc in docs) {
                                listaFinal.add(Pair(categoria, doc.data))
                                Log.d("FIREBASE", "[$categoria] ${doc.id} => ${doc.data}")
                            }
                            verificarSeTerminou(++respostasRecebidas, totalCategorias, listaFinal)
                        }.addOnFailureListener {
                            Log.e("FIREBASE", "Erro ao puxar $categoria", it)
                        }
                }
            }
        }
    }
}
