package com.Burgos.codigosmelon

import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var dao: ProductoDAO
    private lateinit var rvProductos: RecyclerView
    private lateinit var etCodigo: EditText
    private lateinit var etNombre: EditText
    private lateinit var btnInsertar: Button
    private lateinit var btnEditar: Button
    private lateinit var btnEliminar: Button
    private lateinit var searchView: SearchView
    private lateinit var tvEmpty: TextView
    private var listaProductos: MutableList<Producto> = mutableListOf()
    private lateinit var adapter: ProductoAdapter
    private lateinit var emptyView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verificar actualización al iniciar
        UpdateChecker.checkForUpdate(this)

        dao = ProductoDAO(this)

        etCodigo = findViewById(R.id.etCodigo)
        etNombre = findViewById(R.id.etNombre)
        btnInsertar = findViewById(R.id.btnInsertar)
        btnEditar = findViewById(R.id.btnEditar)
        btnEliminar = findViewById(R.id.btnEliminar)
        rvProductos = findViewById(R.id.rvProductos)
        searchView = findViewById(R.id.searchView)
        tvEmpty = findViewById(R.id.tvEmpty)
        emptyView = findViewById(R.id.emptyView)

        rvProductos.layoutManager = LinearLayoutManager(this)

        // Ocultar botones al inicio
        btnEditar.visibility = View.GONE
        btnEliminar.visibility = View.GONE

        listarTodos()

        // Insertar
        btnInsertar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val nombre = etNombre.text.toString()
            if (codigo.isNotEmpty() && nombre.isNotEmpty()) {
                dao.insertar(codigo, nombre)
                Toast.makeText(this, "Insertado", Toast.LENGTH_SHORT).show()
                limpiarCampos()
                listarTodos()
            } else {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Buscar por código o nombre

        // Editar con confirmación
        btnEditar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val nuevoNombre = etNombre.text.toString()

            if (codigo.isNotEmpty() && nuevoNombre.isNotEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmar edición")
                builder.setMessage("¿Seguro que deseas editar el producto con código $codigo y cambiar su nombre a '$nuevoNombre'?")
                builder.setPositiveButton("Sí") { _, _ ->
                    val filas = dao.editar(codigo, nuevoNombre)
                    if (filas > 0) {
                        Toast.makeText(this, "Producto editado", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                        listarTodos()
                    } else {
                        Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                builder.show()
            } else {
                Toast.makeText(this, "Completa código y nombre", Toast.LENGTH_SHORT).show()
            }
        }

        // Eliminar con confirmación
        btnEliminar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            if (codigo.isNotEmpty()) {
                confirmarEliminar(codigo)
            } else {
                Toast.makeText(this, "Ingresa un código", Toast.LENGTH_SHORT).show()
            }
        }

        // SearchView dinámico
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarPorTexto(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarPorTexto(newText)
                return true
            }
        })
    }

    private fun listarTodos() {
        val cursor = dao.listarTodos()
        listaProductos.clear()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID))
            val codigo = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CODIGO))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOMBRE))
            listaProductos.add(Producto(id, codigo, nombre))
        }
        cursor.close()

        if (listaProductos.isEmpty()) {
            rvProductos.adapter = null
            emptyView.visibility = View.VISIBLE   // mostrar mensaje con ícono
        } else {
            adapter = ProductoAdapter(
                productos = listaProductos,
                onEditar = { producto ->
                    etCodigo.setText(producto.codigo)
                    etNombre.setText(producto.nombre)
                    btnEditar.visibility = View.VISIBLE
                    btnEliminar.visibility = View.VISIBLE
                },
                onEliminar = { producto ->
                    confirmarEliminar(producto.codigo)
                }
            )
            rvProductos.adapter = adapter
            emptyView.visibility = View.GONE      // ocultar mensaje cuando hay datos
        }
    }

    private fun limpiarCampos() {
        etCodigo.text.clear()
        etNombre.text.clear()
        btnEditar.visibility = View.GONE
        btnEliminar.visibility = View.GONE
    }

    private fun confirmarEliminar(codigo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Seguro que deseas eliminar el producto con código $codigo?")
        builder.setPositiveButton("Sí") { _, _ ->
            val filas = dao.eliminar(codigo)
            if (filas > 0) {
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                limpiarCampos()
                listarTodos()
            } else {
                Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun filtrarPorTexto(texto: String?) {
        if (texto.isNullOrEmpty()) {
            rvProductos.adapter = adapter
            tvEmpty.visibility = View.GONE
        } else {
            val filtrados = listaProductos.filter {
                it.nombre.contains(texto, ignoreCase = true) ||
                        it.codigo.contains(texto, ignoreCase = true)
            }

            if (filtrados.isEmpty()) {
                rvProductos.adapter = null
                emptyView.visibility = View.VISIBLE   // mostrar mensaje con ícono
            } else {
                rvProductos.adapter = ProductoAdapter(
                    productos = filtrados,
                    onEditar = { producto ->
                        etCodigo.setText(producto.codigo)
                        etNombre.setText(producto.nombre)
                        btnEditar.visibility = View.VISIBLE
                        btnEliminar.visibility = View.VISIBLE
                    },
                    onEliminar = { producto ->
                        confirmarEliminar(producto.codigo)
                    }
                )
                emptyView.visibility = View.GONE      // ocultar mensaje cuando hay resultados
            }
        }
    }
}