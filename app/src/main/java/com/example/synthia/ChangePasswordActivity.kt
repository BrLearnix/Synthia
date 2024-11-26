package com.example.synthia

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.EmailAuthProvider

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var changePasswordButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        auth = FirebaseAuth.getInstance()

        currentPasswordEditText = findViewById(R.id.current_password_edit_text)
        newPasswordEditText = findViewById(R.id.new_password_edit_text)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                changePassword(currentPassword, newPassword)
            } else {
                Toast.makeText(this, "Por favor ingrese la contraseña actual y la nueva", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            // Reautenticar al usuario
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Reautenticación exitosa, actualizar la contraseña
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show()
                                    finish() // Cierra la actividad o redirige a otra pantalla
                                } else {
                                    Toast.makeText(this, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}