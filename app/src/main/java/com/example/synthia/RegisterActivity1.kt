package com.example.synthia

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity1 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)

        val registerText: TextView = findViewById(R.id.textViewLink)
        registerText.setOnClickListener{
            val intent = Intent(this,LoginActivity1::class.java)
            startActivity(intent)
        }

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        val registerButton: TextView = findViewById(R.id.create_account_button)
        registerButton.setOnClickListener {
            performSignUp()
        }

    }

    private fun performSignUp() {
        val username = findViewById<EditText>(R.id.username_edit_text)
        val email = findViewById<EditText>(R.id.email_edit_text)
        val password = findViewById<EditText>(R.id.password_edit_text)
        val dni = findViewById<EditText>(R.id.dni_edit_text)
        val area = findViewById<Spinner>(R.id.work_area_spinner)

        val inputUsername = username.text.toString()
        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()
        val inputDni = dni.text.toString()
        val inputArea = area.selectedItem.toString()

        // Validación del formato del correo electrónico
        if (inputEmail.isEmpty() || inputPassword.isEmpty() || inputUsername.isEmpty() || inputDni.isEmpty()) {
            Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(inputEmail)) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Enviar correo de verificación
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Toast.makeText(this, "Correo de verificación enviado. Por favor revisa tu bandeja de entrada.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Crear datos de usuario para Firestore
                    val userData = hashMapOf(
                        "username" to inputUsername,
                        "email" to inputEmail,
                        "dni" to inputDni,
                        "work_area" to inputArea
                    )

                    // Obtener el ID del usuario actual
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        firestore.collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {

                                Toast.makeText(baseContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity1::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(baseContext, "Error al guardar los datos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Si la autenticación falla, muestra un mensaje al usuario.
                    Toast.makeText(baseContext, "Error de autenticación", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ha ocurrido un error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun isValidEmail(email: String): Boolean {
        // Expresión regular para verificar el formato del correo electrónico
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }

}
