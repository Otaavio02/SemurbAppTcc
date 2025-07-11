package com.otavioaugusto.app_semurb.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.otavioaugusto.app_semurb.dataClasses.DataClassViario

class ViarioDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "viario.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE viario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_sequencial INTEGER,
                tipo TEXT,
                endereco TEXT,
                descricao TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS viario")
        onCreate(db)
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
        }
        db.insert("viario", null, cv)
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

    fun getAllViario(): List<DataClassViario> {
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

}