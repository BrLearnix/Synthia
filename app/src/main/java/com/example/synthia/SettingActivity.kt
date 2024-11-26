package com.example.synthia

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var emailTextView: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailTextView = findViewById(R.id.emailUsuario)

        // Obtener el UID del usuario actual
        val userId = auth.currentUser?.uid
        // Si el usuario está autenticado, obtener sus datos de Firestore
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {

                        val email = document.getString("email")
                        // Mostrar los datos en los TextViews
                        emailTextView.text = email
                    } else {
                        Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener los datos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }



        val logoutButton = findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            // Limpia los datos de sesión (puede variar según tu implementación)
            clearSession()

            // Redirige al usuario a la pantalla de login
            val intent = Intent(this, LoginActivity1::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Ir a cambiar contraseña
        val botonChagePassword: Button = findViewById(R.id.changePasswordButton)
        botonChagePassword.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }


        //el meuno de la pantalla
        // Ir a perfil de usuario
        val menuUser: ImageButton = findViewById(R.id.menuUser)
        menuUser.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        //ir a home
        val menuHome: ImageButton = findViewById(R.id.menuHome)
        menuHome.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }



/**
        // Inicializar SharedPreferences para guardar el tema
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        // Establecer el tema guardado cuando se inicia la actividad
        setAppTheme(sharedPreferences.getString(THEME_PREF, "Sistema"))

        // Inicializar el Spinner para cambiar el tema
        themeSpinner = findViewById(R.id.work_area_spinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.theme_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeSpinner.adapter = adapter

        // Configurar el listener del Spinner
        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                val selectedTheme = parentView.getItemAtPosition(position) as String
                setAppTheme(selectedTheme)
                // Guardar la preferencia del tema
                val editor = sharedPreferences.edit()
                editor.putString(THEME_PREF, selectedTheme)
                editor.apply()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // No hacer nada
            }
        }**/
    }




    //funcion para cerrar sesion
    private fun clearSession() {
        // Ejemplo con SharedPreferences para manejar la sesión
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // Función para cambiar el tema
    private fun setAppTheme(theme: String?) {
        when (theme) {
            "Claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "Sistema" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

}