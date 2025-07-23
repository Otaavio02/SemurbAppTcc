package com.otavioaugusto.app_semurb.fragments


import android.os.Bundle
import android.transition.AutoTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import android.transition.TransitionManager
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.otavioaugusto.app_semurb.R
import com.otavioaugusto.app_semurb.adapters.NotificacoesAdapter
import com.otavioaugusto.app_semurb.dataClasses.DataClassNotificacoes
import com.otavioaugusto.app_semurb.databinding.FragmentNotificacoesBinding
import java.time.LocalDate
import java.time.LocalTime

class NotificacoesFragment : Fragment() {

    private var _binding: FragmentNotificacoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterN: NotificacoesAdapter
    private lateinit var adapterLidas: NotificacoesAdapter
    private lateinit var rvNotificacoesN: RecyclerView
    private lateinit var rvNotificacoesLidas: RecyclerView

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
        _binding = FragmentNotificacoesBinding.inflate(inflater, container, false)

        rvNotificacoesN = binding.rvNotificacoesNLidas
        rvNotificacoesLidas = binding.rvNotificacoesLidas



        adapterN = NotificacoesAdapter(mutableListOf())
        adapterLidas = NotificacoesAdapter(mutableListOf())

        rvNotificacoesN.layoutManager = LinearLayoutManager(requireContext())
        rvNotificacoesLidas.layoutManager = LinearLayoutManager(requireContext())

        rvNotificacoesN.adapter = adapterN
        rvNotificacoesLidas.adapter = adapterLidas


        setupTrocarSlide(rvNotificacoesN, adapterN, adapterLidas)
        setupRemoverSlide(rvNotificacoesLidas, adapterLidas)

        ajustarAlturaRecyclerView(rvNotificacoesN, adapterN.itemCount)
        ajustarAlturaRecyclerView(rvNotificacoesLidas, adapterLidas.itemCount)

        val btnNotificacoesLidas = binding.btnNotificacoesLidas
        val SubtituloLidas = binding.notificacaoLidasSubtitulo
        val btnLixeira = binding.btnLixeira
        btnNotificacoesLidas.isEnabled = adapterLidas.itemCount > 0

        btnNotificacoesLidas.setOnClickListener {
            TransitionManager.beginDelayedTransition(rvNotificacoesLidas, AutoTransition())
            if (rvNotificacoesLidas.isVisible) {
                rvNotificacoesLidas.visibility = View.GONE
                SubtituloLidas.visibility = View.GONE
                btnLixeira.visibility = View.GONE
                btnNotificacoesLidas.setImageResource(R.drawable.notificacaofechado)
            } else {
                rvNotificacoesLidas.visibility = View.VISIBLE
                SubtituloLidas.visibility = View.VISIBLE
                btnLixeira.visibility = View.VISIBLE
                btnNotificacoesLidas.setImageResource(R.drawable.notificacaoaberto)
            }

            ajustarAlturaRecyclerView(rvNotificacoesLidas, adapterLidas.itemCount)
        }

        val btnNotificacoesNLidas = binding.btnNotificacoesNaoLidas
        val SubtituloNLidas = binding.notificacaoNaoLidasSubtitulo

        btnNotificacoesNLidas.setOnClickListener {
            TransitionManager.beginDelayedTransition(rvNotificacoesN, AutoTransition())
            if (rvNotificacoesN.isVisible) {
                rvNotificacoesN.visibility = View.GONE
                SubtituloNLidas.visibility = View.GONE
                btnNotificacoesNLidas.setImageResource(R.drawable.notificacaofechado)
            } else {
                rvNotificacoesN.visibility = View.VISIBLE
                SubtituloNLidas.visibility = View.VISIBLE
                btnNotificacoesNLidas.setImageResource(R.drawable.notificacaoaberto)
            }

            ajustarAlturaRecyclerView(rvNotificacoesN, adapterN.itemCount)
        }



        btnLixeira.setOnClickListener {

            val idUsuario = autenticao.currentUser?.uid
            if (idUsuario != null) {
                bancoDados.collection("agentes")
                    .document(idUsuario)
                    .collection("notificacoes")
                    .whereEqualTo("lida", true)
                    .get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            doc.reference.delete()
                        }


                        adapterLidas.clearAll()
                        ajustarAlturaRecyclerView(rvNotificacoesLidas, 0)
                        Toast.makeText(requireContext(), "Notificações removidas com sucesso", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Erro ao apagar notificações", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        return binding.root
    }

    private fun setupTrocarSlide(
        recyclerView: RecyclerView,
        fonteAdapter: NotificacoesAdapter,
        alvoAdapter: NotificacoesAdapter
    ) {
        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = fonteAdapter.removeItemAt(position)

                val idUsuario = autenticao.currentUser?.uid
                if (idUsuario != null) {
                    val colecao = bancoDados.collection("agentes")
                        .document(idUsuario)
                        .collection("notificacoes")

                    if (item.lida) {
                        colecao
                            .whereEqualTo("titulo", item.titulo)
                            .whereEqualTo("mensagem", item.descricao)
                            .get()
                            .addOnSuccessListener { docs ->
                                for (doc in docs) {
                                    doc.reference.delete()
                                }
                                Toast.makeText(requireContext(), "Notificação removida", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Erro ao remover notificação", Toast.LENGTH_SHORT).show()
                            }


                        ajustarAlturaRecyclerView(rvNotificacoesLidas, fonteAdapter.itemCount)
                    } else {
                        item.lida = true
                        alvoAdapter.addItem(item)

                        colecao
                            .whereEqualTo("titulo", item.titulo)
                            .whereEqualTo("mensagem", item.descricao)
                            .get()
                            .addOnSuccessListener { docs ->
                                for (doc in docs) {
                                    doc.reference.update("lida", true)
                                }
                            }

                        binding.btnNotificacoesLidas.isEnabled = alvoAdapter.itemCount > 0

                        ajustarAlturaRecyclerView(rvNotificacoesN, fonteAdapter.itemCount)
                        ajustarAlturaRecyclerView(rvNotificacoesLidas, alvoAdapter.itemCount)
                    }
                }
            }})

        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    override fun onResume() {
        super.onResume()
        carregarNotificações()
        ajustarAlturaRecyclerView(rvNotificacoesN, adapterN.itemCount)
        ajustarAlturaRecyclerView(rvNotificacoesLidas, adapterLidas.itemCount)
    }

    private fun setupRemoverSlide(
        recyclerView: RecyclerView,
        fonteAdapter: NotificacoesAdapter
    ) {
        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                fonteAdapter.removeItemAt(position)
                ajustarAlturaRecyclerView(rvNotificacoesLidas,fonteAdapter.itemCount )
            } })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun ajustarAlturaRecyclerView(recyclerView: RecyclerView, qtdItens: Int){
        val heighPerItemDp = 260 + 16
        val totalHeightDp = heighPerItemDp * qtdItens

        val scale = resources.displayMetrics.density
        val totalHeightPx = (totalHeightDp * scale).toInt()

        recyclerView.layoutParams?.height = totalHeightPx
        recyclerView.requestLayout()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun carregarNotificações() {
        val idUsuarioLogado = autenticao.currentUser?.uid ?: return


        bancoDados.collection("agentes")
            .document(idUsuarioLogado)
            .collection("notificacoes")
            .whereEqualTo("lida", false)
            .get()
            .addOnSuccessListener { resultado ->
                val notificacoesNLidas = mutableListOf<DataClassNotificacoes>()

                for (document in resultado.documents) {
                    val titulo = document.getString("titulo") ?: ""
                    val mensagem = document.getString("mensagem") ?: ""
                    val horario = document.getString("horario_atual") ?: "00:00"
                    val data = document.getString("dia_atual") ?: "01/01/2000"
                    val lida = document.getBoolean("lida") ?: false

                    val horaSplit = horario.split(":")
                    val dataSplit = data.split("/")
                    val hora = LocalTime.of(horaSplit[0].toInt(), horaSplit[1].toInt())
                    val dataObj = LocalDate.of(dataSplit[2].toInt(), dataSplit[1].toInt(), dataSplit[0].toInt())

                    val notificacao = DataClassNotificacoes(titulo, mensagem, hora, dataObj, lida)
                    notificacoesNLidas.add(notificacao)
                }
                adapterN.setData(notificacoesNLidas)
                ajustarAlturaRecyclerView(rvNotificacoesN, adapterN.itemCount)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao carregar notificações não lidas", Toast.LENGTH_SHORT).show()
            }


        bancoDados.collection("agentes")
            .document(idUsuarioLogado)
            .collection("notificacoes")
            .whereEqualTo("lida", true)
            .get()
            .addOnSuccessListener { resultado ->
                val notificacoesLidas = mutableListOf<DataClassNotificacoes>()

                for (document in resultado.documents) {
                    val titulo = document.getString("titulo") ?: ""
                    val mensagem = document.getString("mensagem") ?: ""
                    val horario = document.getString("horario_atual") ?: "00:00"
                    val data = document.getString("dia_atual") ?: "01/01/2000"
                    val lida = document.getBoolean("lida") ?: false

                    val horaSplit = horario.split(":")
                    val dataSplit = data.split("/")
                    val hora = LocalTime.of(horaSplit[0].toInt(), horaSplit[1].toInt())
                    val dataObj = LocalDate.of(dataSplit[2].toInt(), dataSplit[1].toInt(), dataSplit[0].toInt())

                    val notificacao = DataClassNotificacoes(titulo, mensagem, hora, dataObj, lida)
                    notificacoesLidas.add(notificacao)
                }
                adapterLidas.setData(notificacoesLidas)
                binding.btnNotificacoesLidas.isEnabled = adapterLidas.itemCount > 0
                ajustarAlturaRecyclerView(rvNotificacoesLidas, adapterLidas.itemCount)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao carregar notificações lidas", Toast.LENGTH_SHORT).show()
            }
    }
}

