package com.example.notes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class NotesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notesapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_ISFAVOURITE = "isfavourite"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_CONTENT TEXT,
                $COLUMN_ISFAVOURITE INTEGER,
                $COLUMN_LOCATION TEXT,
                $COLUMN_DATE TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_ISFAVOURITE, note.isFavourite)
            put(COLUMN_LOCATION, note.location)
            put(COLUMN_DATE, note.date)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ISFAVOURITE))
            val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

            val note = Note(id, title, content, isFavourite, location, date)
            notesList.add(note)
        }
        cursor.close()
        db.close()
        return notesList
    }

    fun updateNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_ISFAVOURITE, note.isFavourite)
            put(COLUMN_LOCATION, note.location)
            put(COLUMN_DATE, note.date)
        }
        val whereClause = "$COLUMN_ID=?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun addToFav(note: Note, isFavourite: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ISFAVOURITE, isFavourite)
        }
        val whereClause = "$COLUMN_ID=?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getNoteById(noteId: Int): Note {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID=$noteId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ISFAVOURITE))
        val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
        val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

        cursor.close()
        db.close()
        return Note(id, title, content, isFavourite, location, date)
    }

    fun deleteNote(noteId: Int) {
        Log.d("NotesDatabaseHelper", "Deleting note with ID: $noteId")
        val db = writableDatabase
        val whereClause = "$COLUMN_ID=?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}
