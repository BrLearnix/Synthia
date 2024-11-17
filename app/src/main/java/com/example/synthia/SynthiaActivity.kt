package com.example.synthia

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

class SynthiaActivity : AppCompatActivity(), TextToSpeech.OnInitListener {



    // Declaración de variables
    private lateinit var bufferTextView: TextView

    private lateinit var speechRecognizer: SpeechRecognizer // Objeto para reconocimiento de voz
    private lateinit var tts: TextToSpeech // Objeto para convertir texto a voz
    private var isRecording = false // Bandera para verificar si la grabación está activa
    private var buffer = "" // Variable para almacenar el texto reconocido temporalmente
    private val client = OkHttpClient.Builder() // Cliente para hacer peticiones HTTP
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para conectar (30 segundos)
        .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo máximo para escribir datos
        .readTimeout(30, TimeUnit.SECONDS)    // Tiempo máximo para leer datos
        .build()

    private lateinit var nombreUsuario: String // Variable para el nombre de usuario


    private val messages = mutableListOf( // Lista para almacenar mensajes de la conversación
        mutableMapOf("role" to "system", "content" to "")
    )

    // Método que se ejecuta al crear la actividad
    @SuppressLint("MissingInflatedId", "WrongViewCast")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_synthia)



        bufferTextView = findViewById(R.id.bufferTextView)


        // Obtener el valor de 'nombreUsuario' desde el Intent
        nombreUsuario = intent.getStringExtra("nombre_usuario") ?: "Desconocido" // Default "Desconocido" si es null

        // Construir el contenido del mensaje con el nombreUsuario
        messages[0]["content"] = "" +
                "Tu nombre es: SynthIA, " +
                "su nombre del entrevsitado es: $nombreUsuario. El " +
                "nombre de la empresa es: Br Learnix. " +

                "Eres una entrevistadora IT evaluando a un candidato para una " +
                "posición en React Junior, primero has la bienvenida y luego comienza hacer las preguntas," +
                "la entrevista debe constar de 2 pruguntas, los mensajes no deben contener markdown, emojis, caracters especiales,  las preguntas" +
                "deben ser cortas y concisas, usted toma la desicion de aceptar o" +
                "rechazar al candidato dependiendo a sus respuestas"

        val recordButton = findViewById<Button>(R.id.recordButton) // Obtiene el botón de grabación
        val stopButton = findViewById<ImageButton>(R.id.stopButton) // Obtiene el botón de grabación
        val stopSpeakingButton = findViewById<ImageButton>(R.id.stopButton) // Obtiene el botón de detener voz
        // Verificar permisos de micrófono
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no están concedidos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        // Verificar si TextToSpeech está disponible en el dispositivo
        val checkTTSIntent = Intent()
        checkTTSIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        startActivityForResult(checkTTSIntent, 100)

        // Inicializar el reconocimiento de voz
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(recognitionListener)

        setButtonState(stopButton, false, this)

        // Configurar el botón de grabación
        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording() // Detiene la grabación si está activa
                recordButton.text = "Responder" // Cambia el texto del botón
                StopAnimationRecording()
                setButtonState(stopButton, true, this)


            } else {
                startRecording() // Inicia la grabación si no está activa
                recordButton.text = "Detener" // Cambia el texto del botón
                StartanimationIsRecording()
                setButtonState(stopButton, false, this)

            }
        }
        // Configurar el botón de detener la voz
        stopSpeakingButton.setOnClickListener {
            stopSpeaking() // Detener lo que está diciendo la IA
        }
    }






    // Inicia la grabación de voz
    private fun startRecording() {
        isRecording = true // Cambia el estado a grabando
        buffer = "" // Resetea el buffer de texto
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-PE") // Configura el idioma a español de Perú
        }
        speechRecognizer.startListening(intent) // Inicia el reconocimiento de voz
        showToast("Iniciando grabación") // Muestra un mensaje de inicio de grabación
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
            } else {
                // Permiso denegado
                showToast("Permiso de grabación de audio denegado.")
            }
        }
    }

    // Detiene la grabación de voz
    private fun stopRecording() {
        isRecording = false // Cambia el estado a no grabando
        speechRecognizer.stopListening() // Detiene el reconocimiento de voz
        showToast("Grabación detenida") // Muestra un mensaje de detención de grabación

        // Agrega el mensaje del usuario al historial de mensajes
        messages.add(mapOf("role" to "user", "content" to buffer) as MutableMap<String, String>)

        // Envía el mensaje al servidor
        sendMessageToServer()
    }
    // Envía el mensaje al servidor
    private fun sendMessageToServer() {
        // Crea el JSON que se enviará
        val jsonBody = JSONObject().apply {
            put("model", "llama3.2") // Especifica el modelo a usar
            put("stream", false)
            put("messages", JSONArray(messages)) // Convierte `messages` a un arreglo JSON
        }

        // Configura el cuerpo de la solicitud
        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        // Construye la solicitud HTTP
        val request = Request.Builder()
            .url("http://10.0.2.2:11434/api/chat") // URL del servidor local
            .post(requestBody) // Envía el cuerpo de la solicitud como POST
            .header("Content-Type", "application/json") // Especifica el tipo de contenido
            .build()

        // Envía la solicitud
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showToast("Error al enviar mensaje: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val jsonResponse = JSONObject(responseBody)
                        val messageContent = jsonResponse.getJSONObject("message").getString("content")

                        // Agrega el mensaje de respuesta a `messages`
                        messages.add(mapOf("role" to "assistant", "content" to messageContent) as MutableMap<String, String>)

                        runOnUiThread {
                            speakResponse(messageContent) // Convierte el texto de respuesta en voz
                        }
                    }
                } else {
                    runOnUiThread {
                        showToast("Error en la respuesta de la API: ${response.code}")
                    }
                }
            }
        })
    }

    // Convierte el texto de respuesta en voz
    private fun speakResponse(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Muestra un mensaje emergente en la pantalla
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Listener para el reconocimiento de voz
    private val recognitionListener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                buffer = it.joinToString(" ")
                showToast("mensaje: $buffer")
                bufferTextView.text = buffer

            }
        }

        override fun onError(error: Int) {
            showToast("Error en el reconocimiento de voz: $error")
        }

        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    // Método que se ejecuta al inicializar TextToSpeech
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("es", "PE") // Configura el idioma español (Perú)
            val result = tts.setLanguage(locale)

            // Verifica si el idioma es compatible
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                showToast("El idioma es incompatible o los datos faltan en el dispositivo.")
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                showToast("El idioma no es soportado en este dispositivo.")
            } else {
                showToast("TextToSpeech inicializado correctamente")
            }
        } else {
            // Muestra un mensaje de error detallado
            showToast("Error al inicializar TextToSpeech, código de error: $status")
        }
    }

    // Método que maneja los resultados de las actividades iniciadas para TextToSpeech
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = TextToSpeech(this, this) // Inicializa TextToSpeech si está disponible
            } else {
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent) // Redirige a instalar datos de TTS si faltan
            }
        }
    }

    // Método que se ejecuta al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy() // Libera el reconocedor de voz
        tts.shutdown() // Apaga TextToSpeech
    }

    fun StartanimationIsRecording(){
        // Cargar la animación
        val heartbeatAnimation: Animation =
            AnimationUtils.loadAnimation(this, R.anim.heartbeat_animation)

        // Obtener referencias de las vistas de las barras y aplicar la animación

        // Obtener referencias de las vistas de las barras y aplicar la animación
        val barIds = intArrayOf(
            R.id.view1,
            R.id.view2,
            R.id.view3,
            R.id.view4,
            R.id.view5,
            R.id.view6,
            R.id.view7,
            R.id.view8,
            R.id.view9
        )
        for (id in barIds) {
            val bar = findViewById<View>(id)
            bar.startAnimation(heartbeatAnimation)
        }
    }

    fun StopAnimationRecording() {
        // Obtener referencias de las vistas de las barras y detener la animación
        val barIds = intArrayOf(
            R.id.view1,
            R.id.view2,
            R.id.view3,
            R.id.view4,
            R.id.view5,
            R.id.view6,
            R.id.view7,
            R.id.view8,
            R.id.view9
        )
        for (id in barIds) {
            val bar = findViewById<View>(id)
            bar.clearAnimation() // Detiene la animación en cada barra
        }
    }

    // Función para detener al IA
    fun stopSpeaking() {
        if (this::tts.isInitialized) {
            tts.stop()  // Detiene la reproducción de voz
        }
    }

    fun setButtonState(imageButton: ImageButton, enabled: Boolean, context: Context) {
        imageButton.isClickable = enabled
        imageButton.isEnabled = enabled

        if (enabled) {
            // Restablecer el color y la opacidad cuando esté habilitado
            imageButton.clearColorFilter()
            imageButton.alpha = 1.0f // Sin opacidad (totalmente visible)
        } else {
            // Aplicar un filtro de color gris y reducir la opacidad de la imagen cuando esté deshabilitado
            imageButton.alpha = 0.5f // Reducir la opacidad para que la imagen se vea tenue
        }
    }
}