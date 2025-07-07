package com.otavioaugusto.app_semurb.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.otavioaugusto.app_semurb.dataClasses.DataClassOcorrencia
import com.otavioaugusto.app_semurb.utils.getStringOrNull

class ocorrenciasDBHelper(context: Context) : SQLiteOpenHelper(context, "ocorrencias.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE ocorrencias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT,
                numero_sequencial INTEGER,
                endereco TEXT,
                nome_contato TEXT,
                telefone_contato TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ocorrencias")
        onCreate(db)
    }

    fun insertOcorrencia(tipo: String): Long {
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
        }
        return db.insert("ocorrencias", null, cv)
    }

    fun updateOcorrenciaCompleta(id: Long, tipo: String, endereco: String, nome: String, telefone: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("tipo", tipo)
            put("endereco", endereco)
            put("nome_contato", nome)
            put("telefone_contato", telefone)
        }
        db.update("ocorrencias", cv, "id = ?", arrayOf(id.toString()))
    }

    fun deleteOcorrencia(id: Long) {
        val db = writableDatabase
        db.delete("ocorrencias", "id = ?", arrayOf(id.toString()))
    }

    fun updateEndereco(id: Long, endereco: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("endereco", endereco)
        }
        db.update("ocorrencias", cv, "id = ?", arrayOf(id.toString()))
    }

    fun updateContato(id: Long, nome: String, telefone: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nome_contato", nome)
            put("telefone_contato", telefone)
        }
        db.update("ocorrencias", cv, "id = ?", arrayOf(id.toString()))
    }

    fun getAllOcorrencias(): List<DataClassOcorrencia> {
        val db = readableDatabase
        val cursor = db.query("ocorrencias", null, null, null, null, null, "numero_sequencial ASC")
        val lista = mutableListOf<DataClassOcorrencia>()
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    DataClassOcorrencia(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        numeroSequencial = it.getInt(it.getColumnIndexOrThrow("numero_sequencial")),
                        tipo = it.getStringOrNull("tipo") ?: "",
                        endereco = it.getStringOrNull("endereco") ?: "",
                        nome = it.getStringOrNull("nome_contato") ?: "",
                        numcontato = it.getStringOrNull("telefone_contato") ?: ""
                    )
                )
            }
        }
        return lista
    }

}
