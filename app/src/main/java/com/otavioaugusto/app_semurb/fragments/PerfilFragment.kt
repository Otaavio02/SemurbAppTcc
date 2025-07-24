package com.otavioaugusto.app_semurb.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.otavioaugusto.app_semurb.databinding.FragmentPerfilBinding

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }
    val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)








        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val idUsuarioLogado = autenticacao.currentUser?.uid
        if (idUsuarioLogado != null){
            val referenciaUsuario = bancoDados.collection("agentes")
                .document(idUsuarioLogado)

            referenciaUsuario.get()

                .addOnSuccessListener { documentSnapshot ->
                    Log.i("FIREBASETESTE", "Dados Puxados com Sucesso")
                    val dados = documentSnapshot.data
                    if (dados != null){
                        val nome = dados["nome"]
                        val idade = dados["idade"]
                        val matricula = dados["matricula"]
                        val ocorrencias = dados["ocorrencias"]
                        val viario = dados["viario"]
                        binding.textViewMatricula.text = matricula.toString()
                        binding.textViewNome.text = nome.toString()
                        binding.textViewServicos.text = viario.toString()
                        binding.textViewOcorrencias.text = ocorrencias.toString()


                    }
                }.addOnFailureListener {
                    Log.i("FIREBASETESTE", "Erro ao puxar dados")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}