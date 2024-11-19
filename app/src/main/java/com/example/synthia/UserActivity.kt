package com.example.synthia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvEmailUsuario: TextView
    private lateinit var tvDniUsuario: TextView
    private lateinit var tvAreaTrabajo: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)

        // Obtener los datos pasados desde LoginActivity
        val nombreUsuarioOEmail = intent.getStringExtra("nombre_usuarioOEmail")

        // Inicializar DatabaseHelper
        db = DatabaseHelper(this)

        val usuarioNombre = db.obtenerNombreUsuario(nombreUsuarioOEmail)
        val usuarioEmail = db.obtenerEmail(nombreUsuarioOEmail)
        val usuarioDni = db.obtenerDni(nombreUsuarioOEmail)
        val usuarioArea = db.obtenerArea(nombreUsuarioOEmail)


        // Referencias a los TextViews donde se mostrarán los datos
        tvNombreUsuario = findViewById(R.id.nombreUsuario)
        tvEmailUsuario = findViewById(R.id.emailUsuario)
        tvDniUsuario = findViewById(R.id.dniUsuario)
        tvAreaTrabajo = findViewById(R.id.areaTrabajo)

        if (usuarioNombre != null) {
            tvNombreUsuario.text = "$usuarioNombre"
        } else {
            tvNombreUsuario.text = "Usuario1"
        }

        if (usuarioEmail != null) {
            tvEmailUsuario.text = "$usuarioEmail"
        } else {
            tvEmailUsuario.text = "Ejemplo@gmail.com"
        }

        if (usuarioDni != null) {
            tvDniUsuario.text = "$usuarioDni"
        } else {
            tvDniUsuario.text = "xxxxxxx"
        }
        if (usuarioArea != null) {
            tvAreaTrabajo.text = "$usuarioArea"
        } else {
            tvAreaTrabajo.text = "XXXXXXXXXX"
        }

        //ir a home
        val menuHome: ImageButton = findViewById(R.id.menuHome)

        menuHome.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("nombre_usuarioOEmail", nombreUsuarioOEmail)
            startActivity(intent)
        }

        //ir a configuracion
        val menuSettings: ImageButton = findViewById(R.id.menuSettings)

        menuSettings.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, SettingActivity::class.java)
            intent.putExtra("nombre_usuarioOEmail", nombreUsuarioOEmail)
            startActivity(intent)
        }
    }
}