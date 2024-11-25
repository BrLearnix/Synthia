package com.example.synthia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var usernameTextView: TextView
    private lateinit var userArea: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicializamos Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        usernameTextView = findViewById(R.id.textUserName)
        userArea = findViewById(R.id.textArea)

        // Obtener el UID del usuario actual
        val userId = auth.currentUser?.uid

        // Si el usuario está autenticado, obtener sus datos de Firestore
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        var area = document.getString("work_area")

                        // Mostrar los datos en los TextViews
                        usernameTextView.text = username
                        userArea.text = area

                    } else {
                        Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener los datos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }

        // Ir a SynthiaActivity2
        val buttonAboutCompany: LinearLayout = findViewById(R.id.buttonInterview)
        buttonAboutCompany.setOnClickListener {

            val username = usernameTextView.text.toString()
            val userArea = usernameTextView.text.toString()
            val intent = Intent(this, SynthiaActivity2::class.java)
            intent.putExtra("username", username)
            intent.putExtra("work_area", userArea)  // Enviar el área como extra
            startActivity(intent)
        }

        val buttonSombreCompania: LinearLayout = findViewById(R.id.buttonAboutCompany)
        buttonSombreCompania.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, AreatrabajoActivity::class.java)
            startActivity(intent)
        }

        // Ir a perfil de usuario
        val menuUser: ImageButton = findViewById(R.id.menuUser)
        menuUser.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        // Ir a configuración
        val menuSettings: ImageButton = findViewById(R.id.menuSettings)
        menuSettings.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}
