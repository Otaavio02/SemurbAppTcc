package com.otavioaugusto.app_semurb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


        val idUsuarioLogado = autenticacao.currentUser?.uid
        if (idUsuarioLogado != null){
            val referenciaUsuario = bancoDados.collection("usuarios")
                .document(idUsuarioLogado)

            referenciaUsuario.get()
                .addOnSuccessListener { documentSnapshot ->
                    val dados = documentSnapshot.data
                    if (dados != null){
                        val nome = dados["nome"]
                        val idade = dados["idade"]
                        val matricula = dados["matricula"]
                        val ocorrencias = dados["ocorrencias"]
                        val viario = dados["viario"]

                        binding.textViewMatriculaPerfil.text = matricula.toString()
                    }
                }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}