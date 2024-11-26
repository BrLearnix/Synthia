package com.example.synthia

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AreatrabajoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    private lateinit var tituloTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var iconoAreaImageView: ImageView
    private lateinit var responsabilidadesTextView: TextView
    private lateinit var requisitosTextView: TextView
    private lateinit var beneficiosTextView: TextView

    private var username: String? = null
    private var userArea: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_areatrabajo)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val areatrabajo = findViewById<TextView>(R.id.tituloTextView)

        // Inicialización de los componentes de la interfaz
        tituloTextView = findViewById(R.id.tituloTextView)
        descripcionTextView = findViewById(R.id.descripcionTextView)
        iconoAreaImageView = findViewById(R.id.iconoAreaImageView)
        responsabilidadesTextView = findViewById(R.id.responsabilidadesTextView)
        requisitosTextView = findViewById(R.id.requisitosTextView)
        beneficiosTextView = findViewById(R.id.beneficiosTextView)


        val userId = auth.currentUser?.uid

        // Si el usuario está autenticado, obtener sus datos de Firestore
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Obtener username y area
                        username = document.getString("username")
                        userArea = document.getString("work_area")

                        areatrabajo.text = userArea


                        // Actualizar la información según el área
                        when (userArea) {
                            "React Junior" -> {
                                // Actualizar información para React Junior
                                tituloTextView.text = "React Junior"
                                descripcionTextView.text = "El desarrollador React Junior es responsable de desarrollar interfaces interactivas y dinámicas utilizando React."
                                iconoAreaImageView.setImageResource(R.drawable.react_icon)
                                responsabilidadesTextView.text = "• Desarrollar aplicaciones con React.js.\n• Escribir código limpio y eficiente."
                                requisitosTextView.text = "• Conocimiento de JavaScript y React.\n• Experiencia en el uso de Git."
                                beneficiosTextView.text = "• Trabajo remoto.\n• Oportunidades de capacitación."
                            }
                            "JavaScript Junior" -> {
                                // Actualizar información para JavaScript Junior
                                tituloTextView.text = "JavaScript Junior"
                                descripcionTextView.text = "El desarrollador JavaScript Junior se especializa en crear aplicaciones y sitios web dinámicos utilizando JavaScript."
                                iconoAreaImageView.setImageResource(R.drawable.javascript_icon)
                                responsabilidadesTextView.text = "• Desarrollar aplicaciones web.\n• Optimizar el rendimiento de las aplicaciones."
                                requisitosTextView.text = "• Conocimiento avanzado de JavaScript.\n• Familiaridad con APIs."
                                beneficiosTextView.text = "• Horarios flexibles.\n• Capacitación constante."
                            }
                            "Análisis de Datos" -> {
                                // Actualizar información para Análisis de Datos
                                tituloTextView.text = "Análisis de Datos"
                                descripcionTextView.text = "El analista de datos es responsable de interpretar y analizar grandes volúmenes de datos para apoyar la toma de decisiones."
                                iconoAreaImageView.setImageResource(R.drawable.data_analysis_icon)
                                responsabilidadesTextView.text = "• Analizar y procesar datos.\n• Crear reportes y visualizaciones."
                                requisitosTextView.text = "• Conocimiento en SQL y Python.\n• Experiencia en herramientas de análisis."
                                beneficiosTextView.text = "• Crecimiento profesional.\n• Trabajo colaborativo."
                            }
                            "Marketing" -> {
                                // Actualizar información para Marketing
                                tituloTextView.text = "Marketing"
                                descripcionTextView.text = "El equipo de marketing se encarga de promover productos y servicios, estableciendo estrategias para aumentar el alcance y las ventas."
                                iconoAreaImageView.setImageResource(R.drawable.marketing_icon)
                                responsabilidadesTextView.text = "• Crear campañas publicitarias.\n• Analizar el comportamiento del consumidor."
                                requisitosTextView.text = "• Conocimiento en marketing digital.\n• Creatividad y capacidad analítica."
                                beneficiosTextView.text = "• Horarios flexibles.\n• Oportunidades de promoción interna."
                            }
                            "Diseño Gráfico" -> {
                                // Actualizar información para Diseño Gráfico
                                tituloTextView.text = "Diseño Gráfico"
                                descripcionTextView.text = "El diseñador gráfico se encarga de crear materiales visuales que representan la identidad y visión de la empresa."
                                iconoAreaImageView.setImageResource(R.drawable.graphic_design_icon)
                                responsabilidadesTextView.text = "• Diseñar materiales gráficos.\n• Crear contenido visual para campañas."
                                requisitosTextView.text = "• Experiencia en herramientas de diseño (Photoshop, Illustrator).\n• Creatividad e innovación."
                                beneficiosTextView.text = "• Ambiente creativo.\n• Oportunidades de trabajo remoto."
                            }
                            else -> {
                                // Si no se encuentra el área, podemos mostrar un mensaje por defecto o vacío
                                tituloTextView.text = "Área no definida"
                                descripcionTextView.text = "No hay información disponible para esta área."
                                iconoAreaImageView.setImageResource(R.drawable.synthia)
                                responsabilidadesTextView.text = ""
                                requisitosTextView.text = ""
                                beneficiosTextView.text = ""
                            }
                        }
                    } else {
                        Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener los datos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }

    }
}

