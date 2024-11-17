package com.example.synthia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



class loginActivity : AppCompatActivity() {

    private lateinit var etNombreUsuarioOEmail: EditText
    private lateinit var etContrasena: EditText
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)
        etNombreUsuarioOEmail = findViewById(R.id.username_email_edit_text)
        etContrasena = findViewById(R.id.password_edit_text)

        val btnLogin = findViewById<Button>(R.id.login_button)
        btnLogin.setOnClickListener {

            val nombreUsuarioOEmail = etNombreUsuarioOEmail.text.toString()
            val contrasena = etContrasena.text.toString()


            if (nombreUsuarioOEmail.isNotEmpty() && contrasena.isNotEmpty()) {
                // Validar el usuario
                if (db.validarUsuario(nombreUsuarioOEmail, contrasena)) {


                    // Si la validación es exitosa, pasa al HomeActivity
                    val nombreUsuarioOEmail = db.obtenerNombreUsuario(nombreUsuarioOEmail)
                    //val email = db.obtenerEmail(nombreUsuarioOEmail)
                    //val dniUsuario = db.obtenerDni(nombreUsuarioOEmail)
                    //val areaUsuario = db.obtenerArea(nombreUsuarioOEmail)

                    // Redirigir a HomeActivity con el nombre de usuario y email
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("nombre_usuarioOEmail", nombreUsuarioOEmail)
                    //intent.putExtra("email", email)
                    //intent.putExtra("dni_usuario", dniUsuario)
                    //intent.putExtra("area_trabajo", areaUsuario)

                    Toast.makeText(this, "Sesion Iniciada", Toast.LENGTH_SHORT).show()

                    startActivity(intent)

                } else {
                    // Si las credenciales no son correctas, mostrar un mensaje
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        val getStartedButton: TextView = findViewById(R.id.sign_up_link)
        getStartedButton.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)  // Redirige a la pantalla de login
            startActivity(intent)
        }
    }
}