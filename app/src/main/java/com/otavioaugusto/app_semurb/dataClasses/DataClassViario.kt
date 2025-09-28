package com.otavioaugusto.app_semurb.dataClasses

import java.io.Serializable

data class DataClassViario(
    val id: Long,
    val numeroSequencial: Any?,
    val tipo: String,
    val endereco: String,
    val descricao: String,
    val data_envio: String,
    val foto_url: String? = null
): Serializable