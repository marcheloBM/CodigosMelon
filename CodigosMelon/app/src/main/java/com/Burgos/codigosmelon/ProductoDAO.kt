package com.Burgos.codigosmelon

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import android.widget.Toast


class ProductoDAO(private val context: Context) {

    private val dbHelper = DBHelper(context)

    fun insertar(codigo: String, nombre: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COL_CODIGO, codigo)
            put(DBHelper.COL_NOMBRE, nombre)
        }
        db.insert(DBHelper.TABLE_NAME, null, values)
        db.close()
    }

    fun buscarPorCodigo(codigo: String): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DBHelper.TABLE_NAME,
            null,
            "${DBHelper.COL_CODIGO} = ?",
            arrayOf(codigo),
            null, null, null
        )
    }

    fun listarTodos(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DBHelper.TABLE_NAME,
            null,
            null, null, null, null,
            "${DBHelper.COL_ID} ASC"
        )
    }

    fun eliminar(codigo: String): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete(
            DBHelper.TABLE_NAME,
            "${DBHelper.COL_CODIGO} = ?",
            arrayOf(codigo)
        )
        db.close()
        return filas
    }

    fun editar(codigo: String, nuevoNombre: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COL_NOMBRE, nuevoNombre)
        }
        val filas = db.update(
            DBHelper.TABLE_NAME,
            values,
            "${DBHelper.COL_CODIGO} = ?",
            arrayOf(codigo)
        )
        db.close()
        return filas
    }
}