package com.otavioaugusto.app_semurb.dataClasses

import java.time.LocalTime

data class DataClassHistorico1Ocorrencias(
    val id: Int = 0,
    val numeroSequencial: Int,
    val titulo: String = "",
    val horarioEnvio: String = "",
    val dataEnvio: String = "",
    val tipo: String,
    val endereco: String,
    val nome: String,
    val numcontato: String,
    val quantidadeTotal: Int = 0
) {
}
data class DataClassHistorico1Viario(
    val id: Int = 0,
    val numeroSequencial: Int,
    val titulo: String = "",
    val horarioEnvio: String = "",
    val dataEnvio: String = "",
    val tipo: String,
    val endereco: String,
    val descricao: String,
    val quantidadeTotal: Int = 0

) {
}