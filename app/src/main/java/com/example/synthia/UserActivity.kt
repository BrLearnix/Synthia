package com.example.synthia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var dniTextView: TextView
    private lateinit var areaTextView: TextView

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicializa las vistas
        usernameTextView = findViewById(R.id.nombreUsuario)
        emailTextView = findViewById(R.id.emailUsuario)
        dniTextView = findViewById(R.id.dniUsuario)
        areaTextView = findViewById(R.id.areaTrabajo)

        // Obtener el UID del usuario actual
        val userId = auth.currentUser?.uid

        // Si el usuario está autenticado, obtener sus datos de Firestore
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        val email = document.getString("email")
                        val dni = document.getString("dni")
                        val area = document.getString("work_area")

                        // Mostrar los datos en los TextViews
                        usernameTextView.text = username
                        emailTextView.text = email
                        dniTextView.text = dni
                        areaTextView.text = area
                    } else {
                        Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener los datos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }


        //ir a home
        val menuHome: ImageButton = findViewById(R.id.menuHome)

        menuHome.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        //ir a configuracion
        val menuSettings: ImageButton = findViewById(R.id.menuSettings)

        menuSettings.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}