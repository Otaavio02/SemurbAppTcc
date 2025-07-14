package com.otavioaugusto.app_semurb.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.otavioaugusto.app_semurb.dataClasses.DataClassOcorrencia
import com.otavioaugusto.app_semurb.dataClasses.DataClassViario
import com.otavioaugusto.app_semurb.utils.getStringOrNull
import java.text.SimpleDateFormat
import java.util.*

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_database.db"
        private const val DATABASE_VERSION = 1
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
                titulo TEXT,
                horario_envio TEXT,
                data_envio TEXT,
                enviado INTEGER DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE viario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_sequencial INTEGER,
                tipo TEXT,
                endereco TEXT,
                descricao TEXT,
                titulo TEXT,
                horario_envio TEXT,
                data_envio TEXT,
                enviado INTEGER DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ocorrencias")
        db.execSQL("DROP TABLE IF EXISTS viario")
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
            put("titulo", "Ocorrência ")
        }
        db.insert("ocorrencias", null, cv)
    }

    fun getAllOcorrenciasNaoEnviadas(): List<DataClassOcorrencia> {
        val db = readableDatabase
        val cursor = db.query("ocorrencias", null, "enviado = ?", arrayOf("0"), null, null, "numero_sequencial ASC")
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

    fun getAllOcorrenciasEnviadas(): List<DataClassOcorrencia> {
        val db = readableDatabase
        val cursor = db.query("ocorrencias", null, "enviado = ?", arrayOf("1"), null, null, "numero_sequencial ASC")
        val lista = mutableListOf<DataClassOcorrencia>()
        cursor.use {
            while (it.moveToNext()) {
                val numeroSequencial = it.getInt(it.getColumnIndexOrThrow("numero_sequencial"))
                val titulo = "Ocorrência"
                val horarioEnvio = it.getStringOrNull("horario_envio") ?: ""
                val dataEnvio = it.getStringOrNull("data_envio") ?: ""

                lista.add(
                    DataClassOcorrencia(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        numeroSequencial = numeroSequencial,
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")) ?: "",
                        endereco = it.getStringOrNull("endereco") ?: "",
                        nome = it.getStringOrNull("nome") ?: "",
                        numcontato = it.getStringOrNull("numcontato") ?: "",
                        titulo = titulo,
                        horarioEnvio = horarioEnvio,
                        dataEnvio = dataEnvio
                    )
                )
            }
        }
        return lista
    }

    fun marcarOcorrenciasComoEnviadas(ids: List<Int>) {
        val db = writableDatabase
        val horarioAtual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("enviado", 1)
                put("horario_envio", horarioAtual)
                put("data_envio", dataAtual)
            }
            ids.forEach { id ->
                db.update("ocorrencias", values, "id=?", arrayOf(id.toString()))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
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

    fun deleteOcorrencia(id: Long) {
        val db = writableDatabase
        db.delete("ocorrencias", "id = ?", arrayOf(id.toString()))
    }

    fun deleteAllOcorrencias(){
        val db = writableDatabase
        db.delete("ocorrencias", null, null)
        db.close()
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
            put("titulo", "Viário ")
        }
        db.insert("viario", null, cv)
    }

    fun getAllViariosNaoEnviados(): List<DataClassViario> {
        val db = readableDatabase
        val cursor = db.query("viario", null, null, null, null, null, "numero_sequencial ASC")
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

    fun getAllViarioEnviados(): List<DataClassViario> {
        val db = readableDatabase
        val cursor = db.query("viario", null, "enviado = ?", arrayOf("1"), null, null, "numero_sequencial ASC")
        val lista = mutableListOf<DataClassViario>()
        cursor.use {
            while (it.moveToNext()) {
                val numeroSequencial = it.getInt(it.getColumnIndexOrThrow("numero_sequencial"))
                val titulo = "Viário"
                val horarioEnvio = it.getStringOrNull("horario_envio") ?: ""
                val dataEnvio = it.getStringOrNull("data_envio") ?: ""

                lista.add(
                    DataClassViario(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        numeroSequencial = it.getInt(it.getColumnIndexOrThrow("numero_sequencial")),
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")) ?: "",
                        endereco = it.getString(it.getColumnIndexOrThrow("endereco")) ?: "",
                        descricao = it.getString(it.getColumnIndexOrThrow("descricao")) ?: "",
                        titulo = titulo,
                        horarioEnvio = horarioEnvio,
                        dataEnvio = dataEnvio
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

    fun marcarViarioComoEnviados(ids: List<Int>) {
        val db = writableDatabase
        val horarioAtual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("enviado", 1)
                put("horario_envio", horarioAtual)
                put("data_envio", dataAtual)
            }
            ids.forEach { id ->
                db.update("viario", values, "id=?", arrayOf(id.toString()))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }
}
