package com.otavioaugusto.app_semurb.funcoes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EnviarNotificacaoBd {

    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    private val autenticao by lazy {
        FirebaseAuth.getInstance()
    }

    fun notificacaoOcorrencia(titulo: String, mensagem: String, dia_atual: String,  horario_atual: String, ){
        val idUsuarioLogado = autenticao.currentUser?.uid
        if (idUsuarioLogado != null){
            val notificacao = hashMapOf(
                "titulo" to titulo,
                "mensagem" to mensagem,
                "dia_atual" to dia_atual,
                "horario_atual" to horario_atual,
                "lida" to false
            )
            bancoDados.collection("agentes")
                .document(idUsuarioLogado)
                .collection("notificacoes")
                .add(notificacao)


        }
    }
}