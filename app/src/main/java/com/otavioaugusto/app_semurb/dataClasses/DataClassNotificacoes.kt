package com.otavioaugusto.app_semurb.dataClasses

import java.time.LocalDate
import java.time.LocalTime

data class DataClassNotificacoes(
    val titulo: String,
    val descricao: String,
    val horario: LocalTime,
    val data: LocalDate,
    val lida: Boolean

)