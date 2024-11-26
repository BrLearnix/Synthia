package com.example.synthia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity1 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Vistas del Banner de mensaje
    private lateinit var messageBanner: View
    private lateinit var messageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login1)

        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Encuentra las vistas del banner
        messageBanner = findViewById(R.id.messageBanner)
        messageText = findViewById(R.id.messageText)

        val emailEditText: EditText = findViewById(R.id.username_email_edit_text)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text)
        val loginButton: Button = findViewById(R.id.login_button)
        val registerText: TextView = findViewById(R.id.sign_up_link)

        // Redirige a la pantalla de registro
        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity1::class.java)
            startActivity(intent)
        }

        // Inicia sesión cuando el usuario presiona el botón de login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                // Muestra mensaje de error
                showMessage("Debes rellenar todos los campos", "#FF0000")  // Rojo para error
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            // Inicio de sesión exitoso
                            showMessage("Inicio de sesión exitoso", "#4CAF50")  // Verde para éxito
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Correo no verificado
                            showMessage("Por favor verifica tu correo antes de iniciar sesión.", "#FFEB3B")  // Amarillo para advertencia
                        }
                    } else {
                        // Error en el inicio de sesión
                        showMessage("Inicio de sesión fallido. Verifica tu correo y contraseña.", "#FF0000")  // Rojo para error
                    }
                }
                .addOnFailureListener { e ->
                    // Error general
                    showMessage("Error: ${e.localizedMessage}", "#FF0000")  // Rojo para error
                }
        }
    }

    // Función para mostrar el mensaje
    private fun showMessage(message: String, color: String) {
        messageText.text = message
        messageBanner.setBackgroundColor(android.graphics.Color.parseColor(color))  // Cambia el color de fondo
        messageBanner.visibility = View.VISIBLE

        // Ocultar el banner después de 3 segundos (puedes cambiar este tiempo)
        messageBanner.postDelayed({ messageBanner.visibility = View.GONE }, 3000)
    }
}
