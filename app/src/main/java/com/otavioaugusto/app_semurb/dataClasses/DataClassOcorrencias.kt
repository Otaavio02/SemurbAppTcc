package com.otavioaugusto.app_semurb.dataClasses

import java.io.Serializable

data class DataClassOcorrencia(
    val id: Int = 0,
    val numeroSequencial: Int,
    val tipo: String,
    val endereco: String,
    val nome: String,
    val numcontato: String

): Serializable