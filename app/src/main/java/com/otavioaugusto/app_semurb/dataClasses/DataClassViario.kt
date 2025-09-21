package com.otavioaugusto.app_semurb.dataClasses

data class DataClassViario(
    val id: Any?,
    val numeroSequencial: Any?,
    val tipo: String,
    val endereco: String,
    val descricao: String,
    val data_envio: String,
    //val horario_envio: String, TODO (talvez usar o ID em si?)
    val fotoUrl: String? = null
)