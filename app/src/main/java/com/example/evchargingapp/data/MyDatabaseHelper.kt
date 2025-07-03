package com.example.evchargingapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "MyDatabase.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                email TEXT,
                otp TEXT
            )
        """.trimIndent()

        val createPilesTable = """
        CREATE TABLE IF NOT EXISTS piles (
        id TEXT PRIMARY KEY,
        name TEXT,
        isOnline INTEGER
        )
        """.trimIndent()
        db.execSQL(createPilesTable)

        db.execSQL(createUserTable)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS piles")
        onCreate(db)
    }

    fun insertUser(username: String, email: String, otp: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("otp", otp)
        }
        val result = db.insert("users", null, values)
        return result != -1L
    }

    fun insertPile(id: String, name: String, isOnline: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("name", name)
            put("isOnline", if (isOnline) 1 else 0)
        }
        val result = db.insert("piles", null, values)
        return result != -1L
    }

    fun getAllPiles(): List<ChargingPile> {
        val pileList = mutableListOf<ChargingPile>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM piles", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val isOnline = cursor.getInt(cursor.getColumnIndexOrThrow("isOnline")) == 1

                pileList.add(ChargingPile(id, name, isOnline))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return pileList
    }


    fun getAllUsers(): List<String> {
        val userList = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                userList.add("$name - $email")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }
}
