package com.otavioaugusto.app_semurb.funcoes

import com.google.firebase.auth.FirebaseAuth
import android.util.Log
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

            // Aumenta a quantidade enviada de ocorrencias ou serviço viário no perfil.
            bancoDados.collection("agentes")
                    .document(idUsuarioLogado)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (titulo == "Notificação de Ocorrência") {
                            val fieldQtd = documentSnapshot["ocorrencias"].toString()
                            val NovaQtd = fieldQtd.toInt() + 1
                            Log.d("TESTE", "NOVAQTD: $NovaQtd /// FIELDQTD: $fieldQtd")
                            bancoDados.collection("agentes").document(idUsuarioLogado).update("ocorrencias", NovaQtd.toString())
                        }
                        if (titulo == "Notificação de Viário") {
                            val fieldQtd = documentSnapshot["viario"].toString()
                            val NovaQtd = fieldQtd.toInt() + 1
                            Log.d("TESTE", "NOVAQTD: $NovaQtd /// FIELDQTD: $fieldQtd")
                            bancoDados.collection("agentes").document(idUsuarioLogado).update("viario", NovaQtd.toString())
                        }
                    }

            // Envia a notificação pro agente
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