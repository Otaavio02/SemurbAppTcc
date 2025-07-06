package com.otavioaugusto.app_semurb.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.otavioaugusto.app_semurb.dataClasses.DataOcorrencia

class ocorrenciasDBHelper(context: Context) : SQLiteOpenHelper(context, "ocorrencias.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE ocorrencias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT,
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
        val cv = ContentValues().apply {
            put("tipo", tipo)
        }
        return db.insert("ocorrencias", null, cv)
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

    fun getAllOcorrencias(): List<DataOcorrencia> {
        val db = readableDatabase
        val cursor = db.query("ocorrencias", null, null, null, null, null, "id DESC")
        val lista = mutableListOf<DataOcorrencia>()
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    DataOcorrencia(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")) ?: "",
                        endereco = it.getString(it.getColumnIndexOrThrow("endereco")) ?: "",
                        nome = it.getString(it.getColumnIndexOrThrow("nome_contato")) ?: "",
                        numcontato = it.getString(it.getColumnIndexOrThrow("telefone_contato")) ?: ""
                    )
                )
            }
        }
        return lista
    }
}
