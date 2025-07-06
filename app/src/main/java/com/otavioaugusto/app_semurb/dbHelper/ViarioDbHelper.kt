package com.otavioaugusto.app_semurb.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.otavioaugusto.app_semurb.dataClasses.DataViario

class ViarioDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "viario.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE viario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
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

    fun insertTipoViario(tipo: String): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("tipo", tipo)
        }
        return db.insert("viario", null, cv)
    }

    fun updateEndereco(id: Long, endereco: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("endereco", endereco)
        }
        db.update("viario", cv, "id = ?", arrayOf(id.toString()))
    }

    fun updateDescricao(id: Long, descricao: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("descricao", descricao)
        }
        db.update("viario", cv, "id = ?", arrayOf(id.toString()))
    }

    fun getAllViario(): List<DataViario> {
        val db = readableDatabase
        val cursor = db.query("viario", null, null, null, null, null, "id DESC")
        val lista = mutableListOf<DataViario>()
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    DataViario(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
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