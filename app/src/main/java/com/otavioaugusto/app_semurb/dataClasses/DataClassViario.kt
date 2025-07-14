package com.otavioaugusto.app_semurb.dataClasses

data class DataClassViario(
    val id: Int = 0,
    val numeroSequencial: Int,
    val tipo: String,
    val titulo: String = "",
    val horarioEnvio: String = "",
    val dataEnvio: String = "",
    val endereco: String,
    val descricao: String,
)