package com.otavioaugusto.app_semurb.dataClasses

data class DataClassViario(
    val id: Int = 0,
    val numeroSequencial: Int,
    val tipo: String,
    val endereco: String,
    val descricao: String,
)