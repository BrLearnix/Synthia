package com.example.synthia

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val imageView = findViewById<ImageView>(R.id.myGifImageView)
        val gifDrawable = getDrawable(R.drawable.gif_synthia) as? AnimatedImageDrawable
        gifDrawable?.start()
        imageView.setImageDrawable(gifDrawable)

        val getStartedButton: Button = findViewById(R.id.get_started_button)
        getStartedButton.setOnClickListener {
            val intent = Intent(this,LoginActivity1::class.java)  // Redirige a la pantalla de login
            startActivity(intent)
        }
    }
}