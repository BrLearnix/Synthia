package com.example.synthia
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombreUsuario: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etDNI: EditText
    private lateinit var etArea: Spinner
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textViewLink: TextView = findViewById(R.id.textViewLink)
        textViewLink.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)  // Redirige a la pantalla de login
            startActivity(intent)
            finish()  // Opcional: Para evitar que el usuario regrese a esta pantalla al presionar atrás
        }

        db = DatabaseHelper(this)

        etNombreUsuario = findViewById(R.id.username_edit_text)
        etEmail = findViewById(R.id.email_edit_text)
        etContrasena = findViewById(R.id.password_edit_text)
        etDNI = findViewById(R.id.dni_edit_text)
        etArea = findViewById(R.id.work_area_spinner)

        val btnRegistrar = findViewById<Button>(R.id.create_account_button)
        btnRegistrar.setOnClickListener {
            val nombreUsuario = etNombreUsuario.text.toString()
            val email = etEmail.text.toString()
            val contrasena = etContrasena.text.toString()
            val dni = etDNI.text.toString()
            val area = etArea.selectedItem.toString()

            if (db.insertarUsuario(nombreUsuario, email, contrasena, dni, area)) {
                Toast.makeText(this, "Registro exitoso $nombreUsuario", Toast.LENGTH_SHORT).show()

                // Crea el Intent para redirigir a LoginActivity
                val intent = Intent(this, loginActivity::class.java)

                // Asegúrate de pasar los valores correctamente
                intent.putExtra("nombre_usuario", nombreUsuario)
                intent.putExtra("email", email)

                // Inicia LoginActivity
                startActivity(intent)
                finish()  // Opcional: Para evitar que el usuario regrese a la pantalla de registro
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
