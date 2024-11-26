package com.example.synthia

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var editTextNombre: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextDNI: EditText
    private lateinit var editTextArea: Spinner
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_user)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Vincular los elementos de la vista
        editTextNombre = findViewById(R.id.username_edit_text)
        editTextEmail = findViewById(R.id.email_edit_text)
        editTextDNI = findViewById(R.id.dni_edit_text)
        editTextArea = findViewById(R.id.work_area_spinner)
        buttonSave = findViewById(R.id.buttonSave)

        // Inicializar el Spinner con un adaptador de tipo String
        val areas = listOf("React Junior", "JavaScript Junior", "Analista de Datos", "Marketing", "Diseño Grafico") // Ejemplo de áreas
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, areas)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editTextArea.adapter = spinnerAdapter

        // Obtener el usuario actual
        val user = auth.currentUser

        if (user != null) {
            // Obtener los datos del usuario desde Firestore
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Llenar los campos con los datos actuales
                    editTextNombre.setText(document.getString("username"))
                    editTextEmail.setText(document.getString("email"))
                    editTextDNI.setText(document.getString("dni"))
                    val area = document.getString("work_area")
                    if (area != null) {
                        // Usar getPosition correctamente
                        val position = spinnerAdapter.getPosition(area)
                        editTextArea.setSelection(position)
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // El usuario no está autenticado, redirigir al login
            Toast.makeText(this, "No estás autenticado. Redirigiendo...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity1::class.java)
            startActivity(intent)
            finish()  // Cierra la actividad actual
        }

        // Guardar cambios
        buttonSave.setOnClickListener {
            // Obtener los valores de los campos
            val username = editTextNombre.text.toString()
            val email = editTextEmail.text.toString()
            val dni = editTextDNI.text.toString()
            val area = editTextArea.selectedItem.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && dni.isNotEmpty() && area.isNotEmpty()) {
                // Actualizar los datos del usuario en Firestore
                val updatedUser = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "dni" to dni,
                    "work_area" to area
                )

                val userRef = db.collection("users").document(user!!.uid)
                userRef.update(updatedUser as Map<String, Any>).addOnSuccessListener {
                    Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                    // Ir a perfil de usuario
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish() // Regresar a la pantalla anterior
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
