package com.Burgos.codigosmelon

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        const val DATABASE_NAME = "Codigos.db"
        const val DATABASE_VERSION = 2   // ⚠️ Incrementa solo si cambias la estructura
        const val TABLE_NAME = "productos"
        const val COL_ID = "id"
        const val COL_CODIGO = "codigo"
        const val COL_NOMBRE = "nombre"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_CODIGO TEXT UNIQUE, " +
                    "$COL_NOMBRE TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Ejemplo de migración segura:
        if (oldVersion < 2) {
            // Si en el futuro agregas más cambios:
            //db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN nueva_columna TEXT")
        }
        if(oldVersion < 3){
            // Ejemplo de migración segura:
            // Copiar datos a tabla temporal
            //db.execSQL("CREATE TABLE temp_table AS SELECT * FROM $TABLE_NAME")
            // Borrar tabla original
            //db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            // Crear nueva estructura
            //onCreate(db)
            // Restaurar datos en la nueva tabla
            //db.execSQL("INSERT INTO $TABLE_NAME (codigo, nombre) SELECT codigo, nombre FROM temp_table")
            // Eliminar tabla temporal
            //db.execSQL("DROP TABLE temp_table")

        }
    }
}