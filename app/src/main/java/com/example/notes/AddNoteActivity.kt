package com.example.notes

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import java.io.ByteArrayOutputStream

class AddNoteActivity : AppCompatActivity() {

    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var drawingView: DrawingView
    private lateinit var saveButton: Button
    private lateinit var dbHelper: NotesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        drawingView = findViewById(R.id.drawingView)
        saveButton = findViewById(R.id.saveButton)
        dbHelper = NotesDatabaseHelper(this)

        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = noteTitle.text.toString()
        val content = noteContent.text.toString()
        val drawing = drawingView.getDrawing()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val note = Note(
            id = 0,
            title = title,
            content = content,
            isFavourite = 0,
            location = "", // Add location handling if needed
            date = System.currentTimeMillis().toString(),
            drawing = drawing?.toByteArray() // Convert to byte array if needed
        )

        dbHelper.insertNote(note)
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        finish()
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
