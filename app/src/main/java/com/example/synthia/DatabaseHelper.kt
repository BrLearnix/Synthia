package com.example.synthia

// DatabaseHelper
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "usuarios.db"
        private const val DATABASE_VERSION = 1

        // Tabla de usuarios
        private const val TABLE_USERS = "usuarios"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "nombre_usuario"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "contrasena"
        private const val COLUMN_DNI = "dni"
        private const val COLUMN_AREA = "area"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_DNI TEXT, " +
                "$COLUMN_AREA TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Método para insertar un usuario
    fun insertarUsuario(nombreUsuario: String, email: String, contrasena: String, dni: String, area: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, nombreUsuario)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, contrasena)
            put(COLUMN_DNI, dni)
            put(COLUMN_AREA, area)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L // Si el resultado es -1, la inserción falló
    }

    // Método para validar el login
    fun validarUsuario(nombreUsuarioOEmail: String, contrasena: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE ($COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?) AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(nombreUsuarioOEmail, nombreUsuarioOEmail, contrasena))

        val resultado = cursor.count > 0
        cursor.close()
        db.close()
        return resultado
    }
    // Función para obtener el nombre del usuario
    @SuppressLint("Range")
    fun obtenerNombreUsuario(nombreUsuarioOEmail: String?): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_USERNAME FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(nombreUsuarioOEmail, nombreUsuarioOEmail))

        var nombreUsuario: String? = null
        if (cursor.moveToFirst()) {
            // Obtener el nombre del usuario desde la columna COLUMN_USERNAME
            nombreUsuario = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
        }

        cursor.close()
        db.close()
        return nombreUsuario
    }

    // Función para obtener el correo electrónico del usuario
    @SuppressLint("Range")
    fun obtenerEmail(nombreUsuarioOEmail: String?): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_EMAIL FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(nombreUsuarioOEmail, nombreUsuarioOEmail))

        var email: String? = null
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
        }

        cursor.close()
        db.close()
        return email
    }

    // Función para obtener el dni del usuario
    @SuppressLint("Range")
    fun obtenerDni(nombreUsuarioOEmail: String?): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_DNI FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(nombreUsuarioOEmail, nombreUsuarioOEmail))

        var dni: String? = null
        if (cursor.moveToFirst()) {
            dni = cursor.getString(cursor.getColumnIndex(COLUMN_DNI))
        }

        cursor.close()
        db.close()
        return dni
    }


    // Función para obtener el área del usuario
    @SuppressLint("Range")
    fun obtenerArea(nombreUsuarioOEmail: String?): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_AREA FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? OR $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(nombreUsuarioOEmail, nombreUsuarioOEmail))

        var area: String? = null
        if (cursor.moveToFirst()) {
            // Obtener el área desde la columna COLUMN_AREA
            area = cursor.getString(cursor.getColumnIndex(COLUMN_AREA))
        }

        cursor.close()
        db.close()
        return area
    }


}

