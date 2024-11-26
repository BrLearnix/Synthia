package com.example.synthia

import android.content.Intent
import android.os.Bundle
import android.view.View
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

    // Views for the message banner
    private lateinit var messageBanner: View
    private lateinit var messageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Initialize the message banner
        messageBanner = findViewById(R.id.messageBanner)
        messageText = findViewById(R.id.messageText)

        val registerText: TextView = findViewById(R.id.textViewLink)
        registerText.setOnClickListener{
            val intent = Intent(this, LoginActivity1::class.java)
            startActivity(intent)
        }

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

        // Validate inputs
        if (inputEmail.isEmpty() || inputPassword.isEmpty() || inputUsername.isEmpty() || inputDni.isEmpty()) {
            showMessage("Debes rellenar todos los campos", "#FF0000")  // Red for error
            return
        }

        if (!isValidEmail(inputEmail)) {
            showMessage("Por favor, ingresa un correo electrónico válido", "#FF0000")  // Red for error
            return
        }

        // Firebase sign up process
        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Send verification email
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            showMessage("Usurario creado corectamnete. Correo de verificación enviado. Por favor revisa tu bandeja de entrada.", "#4CAF50")  // Yellow for warning
                        } else {
                            showMessage("Error al enviar el correo de verificación", "#FF0000")  // Red for error
                        }
                    }

                    // Save user data to Firestore
                    val userData = hashMapOf(
                        "username" to inputUsername,
                        "email" to inputEmail,
                        "dni" to inputDni,
                        "work_area" to inputArea
                    )

                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        firestore.collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                showMessage("Registro exitoso", "#4CAF50")  // Green for success
                                val intent = Intent(this, LoginActivity1::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                showMessage("Error al guardar los datos: ${e.localizedMessage}", "#FF0000")  // Red for error
                            }
                    }
                } else {
                    showMessage("Error de autenticación", "#FF0000")  // Red for error
                }
            }
            .addOnFailureListener { e ->
                showMessage("Ha ocurrido un error: ${e.localizedMessage}", "#FF0000")  // Red for error
            }
    }

    private fun isValidEmail(email: String): Boolean {
        // Regular expression to check if the email format is valid
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }

    private fun showMessage(message: String, color: String) {
        messageText.text = message
        messageBanner.setBackgroundColor(android.graphics.Color.parseColor(color))  // Set the background color
        messageBanner.visibility = View.VISIBLE

        // Hide the banner after 3 seconds (you can adjust the time)
        messageBanner.postDelayed({ messageBanner.visibility = View.GONE }, 3000)
    }
}
