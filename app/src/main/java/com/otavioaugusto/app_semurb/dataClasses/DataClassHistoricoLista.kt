package com.otavioaugusto.app_semurb.dataClasses

data class DataClassHistorico(
    val id_lista: Int,
    val topico: String,
    val qtd_itens: Int,
    val horario_envio: String,
    val data_envio: String
)