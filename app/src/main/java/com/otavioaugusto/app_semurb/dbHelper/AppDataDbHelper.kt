package com.otavioaugusto.app_semurb.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.otavioaugusto.app_semurb.dataClasses.DataClassHistorico
import com.otavioaugusto.app_semurb.dataClasses.DataClassOcorrencia
import com.otavioaugusto.app_semurb.dataClasses.DataClassViario
import com.otavioaugusto.app_semurb.utils.getStringOrNull

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_database.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE ocorrencias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT,
                numero_sequencial INTEGER,
                endereco TEXT,
                nome TEXT,
                numcontato TEXT,
                topico TEXT,
                id_lista INTEGER,
                FOREIGN KEY (id_lista) REFERENCES lista_historico(id_lista)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE viario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_sequencial INTEGER,
                tipo TEXT,
                endereco TEXT,
                descricao TEXT,
                topico TEXT,
                id_lista INTEGER,
                FOREIGN KEY (id_lista) REFERENCES lista_historico(id_lista)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE lista_historico (
                id_lista INTEGER PRIMARY KEY AUTOINCREMENT,
                topico TEXT, -- "Atendimento de Ocorrências", "Serviço Viário", "Inspeção da Viatura".
                qtd_itens INTEGER,
                horario_envio TEXT,
                data_envio TEXT
            )
        """.trimIndent())

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ocorrencias")
        db.execSQL("DROP TABLE IF EXISTS viario")
        db.execSQL("DROP TABLE IF EXISTS lista_historico")
        onCreate(db)
    }


    fun insertOcorrencia(tipo: String?, endereco: String?, nome: String?, numcontato: String?) {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT MAX(numero_sequencial) FROM ocorrencias", null)
        var proximoNumero = 1
        cursor.use {
            if (it.moveToFirst()) {
                val maxNum = it.getInt(0)
                if (maxNum > 0) proximoNumero = maxNum + 1
            }
        }

        val cv = ContentValues().apply {
            put("tipo", tipo)
            put("numero_sequencial", proximoNumero)
            put("endereco", endereco)
            put("nome", nome)
            put("numcontato", numcontato)
            put("topico", "Atendimento de Ocorrências")
            putNull("id_lista")
        }
        db.insert("ocorrencias", null, cv)
    }

    fun deleteOcorrencia(id: Long) {
        val db = writableDatabase
        db.delete("ocorrencias", "id = ?", arrayOf(id.toString()))
    }

    fun associarOcorrenciasALista(idLista: Long) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_lista", idLista)
        }
        db.update("ocorrencias", values, "id_lista IS NULL AND topico = ?", arrayOf("Atendimento de Ocorrências"))
    }

    fun getAllOcorrenciasNaoEnviadas(): List<DataClassOcorrencia> {
        val db = readableDatabase
        val cursor = db.query("ocorrencias", null, "id_lista IS NULL", null, null, null, "numero_sequencial ASC")
        val lista = mutableListOf<DataClassOcorrencia>()
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    DataClassOcorrencia(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        numeroSequencial = it.getInt(it.getColumnIndexOrThrow("numero_sequencial")),
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")) ?: "",
                        endereco = it.getStringOrNull("endereco") ?: "",
                        nome = it.getStringOrNull("nome") ?: "",
                        numcontato = it.getStringOrNull("numcontato") ?: ""
                    )
                )
            }
        }
        return lista
    }

    fun getAllOcorrenciasByIdLista(id_lista: String?): List<DataClassOcorrencia> {
        val db = readableDatabase
        val cursor = db.query("ocorrencias", null, "id_lista = ?", arrayOf(id_lista), null, null, "numero_sequencial ASC")
        val lista = mutableListOf<DataClassOcorrencia>()
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    DataClassOcorrencia(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        numeroSequencial = it.getInt(it.getColumnIndexOrThrow("numero_sequencial")),
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")) ?: "",
                        endereco = it.getStringOrNull("endereco") ?: "",
                        nome = it.getStringOrNull("nome") ?: "",
                        numcontato = it.getStringOrNull("numcontato") ?: ""
                    )
                )
            }
        }
        return lista
    }

    fun updateOcorrenciaCompleta(id: Long, tipo: String, endereco: String, nome: String, numcontato: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("tipo", tipo)
            put("endereco", endereco)
            put("nome", nome)
            put("numcontato", numcontato)
        }
        db.update("ocorrencias", cv, "id = ?", arrayOf(id.toString()))
    }



    fun insertViarioCompleto(tipo: String?, endereco: String?, descricao: String?) {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT MAX(numero_sequencial) FROM viario", null)
        var proximoNumero = 1
        cursor.use {
            if (it.moveToFirst()) {
                val maxNum = it.getInt(0)
                if (maxNum > 0) proximoNumero = maxNum + 1
            }
        }

        val cv = ContentValues().apply {
            put("tipo", tipo)
            put("numero_sequencial", proximoNumero)
            put("endereco", endereco)
            put("descricao", descricao)
            put("topico", "Serviço Viário")
            putNull("id_lista")
        }
        db.insert("viario", null, cv)
    }

    fun getAllViariosNaoEnviados(): List<DataClassViario> {
        val db = readableDatabase
        val cursor = db.query("viario", null, "id_lista IS NULL", null, null, null, "numero_sequencial ASC")
        val lista = mutableListOf<DataClassViario>()
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    DataClassViario(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        numeroSequencial = it.getInt(it.getColumnIndexOrThrow("numero_sequencial")),
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")) ?: "",
                        endereco = it.getString(it.getColumnIndexOrThrow("endereco")) ?: "",
                        descricao = it.getString(it.getColumnIndexOrThrow("descricao")) ?: ""
                    )
                )
            }
        }
        return lista
    }

    fun getAllViariosByIdLista(id_lista: String?): List<DataClassViario> {
        val db = readableDatabase
        val cursor = db.query("viario", null, "id_lista = ?", arrayOf(id_lista), null, null, "numero_sequencial ASC")
        val lista = mutableListOf<DataClassViario>()
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    DataClassViario(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        numeroSequencial = it.getInt(it.getColumnIndexOrThrow("numero_sequencial")),
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")) ?: "",
                        endereco = it.getString(it.getColumnIndexOrThrow("endereco")) ?: "",
                        descricao = it.getString(it.getColumnIndexOrThrow("descricao")) ?: ""
                    )
                )
            }
        }
        return lista
    }


    fun updateViarioCompleto(id: Long, tipo: String, endereco: String, descricao: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("tipo", tipo)
            put("endereco", endereco)
            put("descricao", descricao)
        }
        db.update("viario", cv, "id = ?", arrayOf(id.toString()))
    }

    fun deleteViario(id: Long) {
        val db = writableDatabase
        db.delete("viario", "id = ?", arrayOf(id.toString()))
    }

    fun associarViarioALista(idLista: Long) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_lista", idLista)
        }
        db.update("viario", values, "id_lista IS NULL AND topico = ?", arrayOf("Serviço Viário"))
    }

    fun insertListaHistorico(topico: String, qtd_itens: Int, horario_envio: String, data_envio: String): Long{
        val db = writableDatabase

        val cv = ContentValues().apply {
            put("topico", topico)
            put("qtd_itens", qtd_itens)
            put("horario_envio", horario_envio)
            put("data_envio", data_envio)
        }
        return db.insert("lista_historico", null, cv)
    }

   /* fun getAllHistorico(): List<DataClassHistorico> {
        val historicoList = mutableListOf<DataClassHistorico>()
        val db = readableDatabase

        val cursor = db.rawQuery("""
        SELECT id_lista, topico, qtd_itens, horario_envio, data_envio
        FROM lista_historico
        ORDER BY data_envio DESC, horario_envio DESC
    """.trimIndent(), null)

        if (cursor.moveToFirst()) {
            do {
                val idLista = cursor.getInt(cursor.getColumnIndexOrThrow("id_lista"))
                val topico = cursor.getString(cursor.getColumnIndexOrThrow("topico"))
                val qtdItens = cursor.getInt(cursor.getColumnIndexOrThrow("qtd_itens"))
                val horario = cursor.getString(cursor.getColumnIndexOrThrow("horario_envio"))
                val data = cursor.getString(cursor.getColumnIndexOrThrow("data_envio"))

                historicoList.add(
                    DataClassHistorico(
                        id_lista = idLista,
                        topico = topico,
                        qtd_itens = qtdItens,
                        horario_envio = horario,
                        data_envio = data
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return historicoList
    }*/

}
