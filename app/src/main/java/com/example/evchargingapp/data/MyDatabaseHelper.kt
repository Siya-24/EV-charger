package com.example.evchargingapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLite helper class to manage local database for users and charging piles.
 * Supports user-scoped piles by storing user_id alongside pile id.
 */
class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "MyDatabase.db", null, 2) {  // DB version 2 for schema update

    override fun onCreate(db: SQLiteDatabase) {
        // Create 'users' table
        val createUserTable = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                email TEXT
            )
        """.trimIndent()

        // Create 'piles' table with composite primary key (id, user_id)
        val createPilesTable = """
            CREATE TABLE IF NOT EXISTS piles (
                id TEXT,
                user_id TEXT,
                name TEXT,
                isOnline INTEGER,
                PRIMARY KEY(id, user_id)
            )
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createPilesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop old tables and recreate on version upgrade (destructive migration)
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS piles")
        onCreate(db)
    }

    /**
     * Insert a new user into the users table.
     * Returns true if insert was successful, false otherwise.
     */
    fun insertUser(username: String, email: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
        }
        val result = db.insert("users", null, values)
        return result != -1L
    }

    /**
     * Insert or replace a charging pile for a specific user.
     * Uses composite primary key (id, user_id) to avoid duplicates across users.
     */
    fun insertPile(userId: String, id: String, name: String, isOnline: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("id", id)
            put("name", name)
            put("isOnline", if (isOnline) 1 else 0)
        }
        // REPLACE will insert or update existing pile for the same id and user_id
        val result = db.replace("piles", null, values)
        return result != -1L
    }

    /**
     * Get all charging piles for a given user from the local database.
     */
    fun getAllPiles(userId: String): List<ChargingPile> {
        val pileList = mutableListOf<ChargingPile>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM piles WHERE user_id = ?", arrayOf(userId))

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

    /**
     * Check if a pile with a given id exists for a specific user.
     */
    fun pileExists(userId: String, id: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM piles WHERE id = ? AND user_id = ?",
            arrayOf(id, userId)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }
}
