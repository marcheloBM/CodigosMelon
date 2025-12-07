package com.Burgos.codigosmelon

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dao: ProductoDAO
    private lateinit var rvProductos: RecyclerView
    private lateinit var etCodigo: EditText
    private lateinit var etNombre: EditText
    private lateinit var btnInsertar: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnEditar: Button
    private lateinit var btnEliminar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dao = ProductoDAO(this)

        etCodigo = findViewById(R.id.etCodigo)
        etNombre = findViewById(R.id.etNombre)
        btnInsertar = findViewById(R.id.btnInsertar)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnEditar = findViewById(R.id.btnEditar)
        btnEliminar = findViewById(R.id.btnEliminar)
        rvProductos = findViewById(R.id.rvProductos)
        rvProductos.layoutManager = LinearLayoutManager(this)

        listarTodos()

        btnInsertar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val nombre = etNombre.text.toString()
            if (codigo.isNotEmpty() && nombre.isNotEmpty()) {
                dao.insertar(codigo, nombre)
                Toast.makeText(this, "Insertado", Toast.LENGTH_SHORT).show()
                listarTodos()
            } else {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnBuscar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val cursor = dao.buscarPorCodigo(codigo)
            val lista = mutableListOf<Producto>()
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOMBRE))
                lista.add(Producto(id, codigo, nombre))
                Toast.makeText(this, "Encontrado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No encontrado", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
            rvProductos.adapter = ProductoAdapter(lista, onEditar = {}, onEliminar = {})
        }

        btnEditar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val nuevoNombre = etNombre.text.toString()
            val filas = dao.editar(codigo, nuevoNombre)
            if (filas > 0) {
                Toast.makeText(this, "Producto editado", Toast.LENGTH_SHORT).show()
                listarTodos()
            } else {
                Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show()
            }
        }

        btnEliminar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val filas = dao.eliminar(codigo)
            if (filas > 0) {
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                listarTodos()
            } else {
                Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun listarTodos() {
        val cursor = dao.listarTodos()
        val lista = mutableListOf<Producto>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID))
            val codigo = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CODIGO))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOMBRE))
            lista.add(Producto(id, codigo, nombre))
        }
        cursor.close()

        val adapter = ProductoAdapter(
            productos = lista,
            onEditar = { producto ->
                etCodigo.setText(producto.codigo)
                etNombre.setText(producto.nombre)
                Toast.makeText(this, "Edita y presiona 'Editar producto'", Toast.LENGTH_SHORT).show()
            },
            onEliminar = { producto ->
                dao.eliminar(producto.codigo)
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                listarTodos()
            }
        )

        rvProductos.adapter = adapter
    }
}