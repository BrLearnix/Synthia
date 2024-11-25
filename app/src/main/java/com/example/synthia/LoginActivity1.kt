package com.example.synthia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity1 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login1)


        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.username_email_edit_text)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text)
        val loginButton: Button = findViewById(R.id.login_button)
        val registerText: TextView = findViewById(R.id.sign_up_link)


        // Si el usuario ya está autenticado, redirigir a la pantalla principal
        /**if (auth.currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }**/
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
                Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Iniciar sesión con correo y contraseña
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Si el inicio de sesión es exitoso, redirige al HomeActivity
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Si el correo no está verificado, muestra un mensaje
                            Toast.makeText(this, "Por favor verifica tu correo antes de iniciar sesión.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Si falla el inicio de sesión, muestra un mensaje de error
                        Toast.makeText(this, "Inicio de sesión fallido. Verifica tu correo y contraseña.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
