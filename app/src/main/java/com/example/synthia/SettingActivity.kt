package com.example.synthia

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        val nombreUsuarioOEmail = intent.getStringExtra("nombre_usuarioOEmail")

        //ir a home
        val menuHome: ImageButton = findViewById(R.id.menuHome)

        menuHome.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("nombre_usuarioOEmail", nombreUsuarioOEmail)
            startActivity(intent)
        }

        //ir a perfil
        val menuUser: ImageButton = findViewById(R.id.menuUser)
        menuUser.setOnClickListener {
            // Crea un Intent para iniciar la nueva actividad
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra("nombre_usuarioOEmail", nombreUsuarioOEmail)
            startActivity(intent)
        }
    }
}