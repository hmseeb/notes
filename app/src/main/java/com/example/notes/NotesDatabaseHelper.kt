package com.example.notes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_ISFAVOURITE = "isfavourite"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_DRAWING = "drawing"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_TITLE TEXT,
                $COLUMN_CONTENT TEXT,
                $COLUMN_ISFAVOURITE INTEGER,
                $COLUMN_LOCATION TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_DRAWING BLOB
            )
        """
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_DRAWING BLOB")
        }
    }
    fun getFavoriteNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ISFAVOURITE = 1"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ISFAVOURITE))
            val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val drawing = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_DRAWING))

            val note = Note(
                id = id,
                title = title,
                content = content,
                isFavourite = isFavourite,
                location = location,
                date = date,
                drawing = drawing
            )
            notesList.add(note)
        }
        cursor.close()
        db.close()
        return notesList
    }
    fun insertNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_ISFAVOURITE, note.isFavourite)
            put(COLUMN_LOCATION, note.location)
            put(COLUMN_DATE, note.date)
            put(COLUMN_DRAWING, note.drawing)
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
            val drawing = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_DRAWING))

            val note = Note(
                id = id,
                title = title,
                content = content,
                isFavourite = isFavourite,
                location = location,
                date = date,
                drawing = drawing
            )
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
            put(COLUMN_DRAWING, note.drawing)
        }
        val whereClause = "$COLUMN_ID=?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun deleteNote(noteId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID=?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun getNoteByID(noteId: Int): Note {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(noteId.toString()),
            null,
            null,
            null
        )

        cursor?.moveToFirst()
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ISFAVOURITE))
        val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
        val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
        val drawing = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_DRAWING))

        cursor.close()
        db.close()

        return Note(
            id = id,
            title = title,
            content = content,
            isFavourite = isFavourite,
            location = location,
            date = date,
            drawing = drawing
        )
    }
}
