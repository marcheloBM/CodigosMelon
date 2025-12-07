package com.Burgos.codigosmelon

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var dao: ProductoDAO
    private lateinit var rvProductos: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Verificar actualización al iniciar
        UpdateChecker.checkForUpdate(this)

        dao = ProductoDAO(this)

        val etCodigo = findViewById<EditText>(R.id.etCodigo)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val btnInsertar = findViewById<Button>(R.id.btnInsertar)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)


        btnInsertar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val nombre = etNombre.text.toString()
            dao.insertar(codigo, nombre)
            Toast.makeText(this, "Insertado", Toast.LENGTH_SHORT).show()
        }

        btnBuscar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val cursor = dao.buscarPorCodigo(codigo)
            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOMBRE))
                etNombre.setText(nombre)
                Toast.makeText(this, "Encontrado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No encontrado", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
        }
        btnBuscar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val cursor = dao.buscarPorCodigo(codigo)
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOMBRE))
                tvResultado.text = "ID: $id\nCódigo: $codigo\nNombre: $nombre"
            } else {
                tvResultado.text = "No se encontró ningún producto con ese código."
            }
            cursor.close()
        }

    }
}
