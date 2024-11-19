package com.example.synthia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var db: DatabaseHelper


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val nombreUsuarioOEmail = intent.getStringExtra("nombre_usuarioOEmail")

        // Inicializar DatabaseHelper
        db = DatabaseHelper(this)

        val usuarioNombre = db.obtenerNombreUsuario(nombreUsuarioOEmail)
        val areaUsuario= db.obtenerArea(nombreUsuarioOEmail)


        tvNombreUsuario = findViewById(R.id.textUserName)
        // Mostrar el nombre de usuario y email si existen
        tvNombreUsuario.text = "$usuarioNombre"


        //ir a Synthia
        val buttonAboutCompany: LinearLayout = findViewById(R.id.buttonInterview)
        buttonAboutCompany.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, SynthiaActivity2::class.java)
            intent.putExtra("nombre_usuario", usuarioNombre)
            intent.putExtra("area_usuario", areaUsuario)
            startActivity(intent)
        }

        val buttonSombreCompania: LinearLayout = findViewById(R.id.buttonAboutCompany)
        buttonSombreCompania.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, AreatrabajoActivity::class.java)
            startActivity(intent)
        }


        //ir a perfil de usuario
        val menuUser: ImageButton = findViewById(R.id.menuUser)
        menuUser.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, UserActivity::class.java)
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