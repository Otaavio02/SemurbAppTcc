package com.otavioaugusto.app_semurb.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.otavioaugusto.app_semurb.dataClasses.DataClassOcorrencia
import com.otavioaugusto.app_semurb.utils.getStringOrNull

class ocorrenciasDBHelper(context: Context) : SQLiteOpenHelper(context, "ocorrencias.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(/* sql = */ """
            CREATE TABLE ocorrencias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT,
                numero_sequencial INTEGER,
                endereco TEXT,
                nome TEXT,
                numcontato TEXT,
                ENVIADO INTEGER DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ocorrencias")
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
        }
        db.insert("ocorrencias", null, cv)
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
            put("nome", nome)
            put("numcontato", telefone)
        }
        db.update("ocorrencias", cv, "id = ?", arrayOf(id.toString()))
    }

    fun getAllOcorrenciasNaoEnviadas(): List<DataClassOcorrencia> {
        val db = readableDatabase
        val cursor = db.query( "ocorrencias",
            null,
            "enviado = ?",
            arrayOf("0"),
            null,
            null,
            "numero_sequencial ASC")
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

    fun marcarOcorrenciasComoEnviadas() {
        val db = writableDatabase
        db.execSQL("UPDATE ocorrencias SET enviado = 1 WHERE enviado = 0")
    }

}