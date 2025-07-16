package com.otavioaugusto.app_semurb.dataClasses

sealed class DataClassHistoricoListItem {
    data class Header(val data: String) : DataClassHistoricoListItem()
    data class Item(val historico: DataClassHistorico) : DataClassHistoricoListItem()
}