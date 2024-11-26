package com.example.synthia
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
class SynthiaActivity2 : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    private lateinit var messageInput: EditText
    private lateinit var requestText: TextView
    private lateinit var responseText: TextView
    private lateinit var voiceButton: Button
    private lateinit var textToSpeech: TextToSpeech

    // Variables para almacenar los datos obtenidos
    private var username: String? = null
    private var userArea: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synthia2)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        messageInput = findViewById(R.id.messageInput)
        requestText = findViewById(R.id.requestText)
        responseText = findViewById(R.id.responseText)
        voiceButton = findViewById(R.id.voiceButton)

        val userId = auth.currentUser?.uid

        // Si el usuario está autenticado, obtener sus datos de Firestore
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Obtener username y area
                        username = document.getString("username")
                        userArea = document.getString("work_area")
                    } else {
                        Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener los datos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }

        textToSpeech = TextToSpeech(this, this)

        voiceButton.setOnClickListener { startVoiceRecognition() }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Configurar el idioma a español de Perú
            val locale = Locale("es", "PE")
            val result = textToSpeech.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                responseText.text = "Idioma no soportado o datos faltantes para español de Perú."
            }
        } else {
            responseText.text = "Error al inicializar el TTS."
        }
    }
    private fun speakResponse(response: String) {
        // Convertir el texto de la respuesta a voz
        textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")
        }

        if (packageManager.queryIntentActivities(intent, 0).isNotEmpty()) {
            startActivityForResult(intent, REQUEST_CODE_SPEECH)
        } else {
            responseText.text = "Reconocimiento de voz no disponible en este dispositivo."
        }
    }
    private val REQUEST_CODE_SPEECH = 100
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH && resultCode == RESULT_OK) {
            val recognizedText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            if (recognizedText.isNotEmpty()) {
                var requestMessage = requestText.setText(recognizedText)
                messageInput.setText(recognizedText)
                sendMessage(recognizedText) { response ->
                    runOnUiThread {
                        responseText.text = response
                        var responseMesssage = speakResponse(response)
                        messageInput.setText("")
                    }
                }
            } else {
                responseText.text = "No se pudo reconocer ningún texto."
            }
        }
    }

    private fun sendMessage(message: String, callback: (String) -> Unit) {
        val jsonBody = JSONObject().apply {
            put("model", "llama3.2")
            put("stream", false)

            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "Tu nombre es: SynthIA, una entrevistadora especializada y encargada de evaluar candidatos para una posición de trabajo en $userArea. " +
                            "Su nombre del entrevistado es: $username. La entrevista debe desarrollarse de manera profesional y estructurada, siguiendo estas reglas:\n" +
                            "* Haz dos preguntas técnicas relevantes y prácticas, enfocadas en evaluar conocimientos fundamentales y habilidades aplicadas.\n" +
                            "* Cada pregunta debe ser clara, breve y enfocada en problemas reales o situaciones comunes en el desarrollo\n" +
                            "* Evalúa cada respuesta del candidato de forma directa: responde únicamente con \"(respuesta correcta)\" o \"(respuesta incorrecta)\", sin explicaciones adicionales.\n" +
                            "* Si la respuesta es incorrecta, pasa directamente a la siguiente pregunta sin ofrecer pistas ni detalles.\n" +
                            "* Basándote en las respuestas proporcionadas, al final decide si el candidato es adecuado para la posición y comunica tu decisión con una breve justificación profesional.\n" +
                            "* Termina la entrevista con un mensaje de despedida cordial y alentador.\n" +
                            "* Mantén un tono profesional y directo durante toda la interacción. Evita el uso de emojis, markdown, o texto innecesario.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", message)
                })
            }

            put("messages", messagesArray)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("http://10.0.2.2:11434/api/chat")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val messageContent = try {
                        JSONObject(responseBody).getJSONObject("message").getString("content")
                    } catch (e: Exception) {
                        "Error al procesar la respuesta: ${e.message}"
                    }
                    callback(messageContent)

                    // Guardar conversación en Firestore
                    saveMessageToFirestore(message, "user") // Guardar el mensaje del usuario
                    saveMessageToFirestore(messageContent, "ia") // Guardar la respuesta de la IA
                } else {
                    callback("Error: ${response.message}")
                }
            }
        })
    }

    private fun saveMessageToFirestore(messageContent: String, messageType: String) {
        val userId = auth.currentUser?.uid
        val timestamp = System.currentTimeMillis() // Usamos la hora actual como marca temporal

        if (userId != null) {
            val messageData = hashMapOf(
                "user_id" to userId,
                "timestamp" to timestamp,
                "message_type" to messageType,
                "message_content" to messageContent
            )

            // Guardar el mensaje en la colección "conversations"
            db.collection("conversations")
                .add(messageData)
                .addOnSuccessListener { documentReference ->
                    Log.d("SynthiaActivity2", "Mensaje guardado con ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("SynthiaActivity2", "Error al guardar el mensaje", e)
                }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos de TextToSpeech al destruir la actividad
        textToSpeech.shutdown()
    }
}