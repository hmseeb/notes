package com.example.notes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var drawingView: DrawingView
    private lateinit var saveButton: FloatingActionButton
    private lateinit var dbHelper: NotesDatabaseHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        drawingView = findViewById(R.id.drawingView)
        saveButton = findViewById(R.id.saveButton)
        dbHelper = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            val note = dbHelper.getNoteById(noteId)
            noteTitle.setText(note.title)
            noteContent.setText(note.content)
            if (note.drawing != null) {
                val bitmap = BitmapFactory.decodeByteArray(note.drawing, 0, note.drawing.size)
                drawingView.setBitmap(bitmap)
            }
        }

        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = noteTitle.text.toString()
        val content = noteContent.text.toString()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val drawing = getDrawingByteArray(drawingView.getBitmap())

        if (noteId == -1) {
            val note = Note(
                id = 0,
                title = title,
                content = content,
                isFavourite = 0,
                location = "", // Placeholder, will be updated later
                date = date,
                drawing = drawing
            )
            dbHelper.insertNote(note)
        } else {
            val note = Note(
                id = noteId,
                title = title,
                content = content,
                isFavourite = 0,
                location = "", // Placeholder, will be updated later
                date = date,
                drawing = drawing
            )
            dbHelper.updateNote(note)
        }
        finish()
    }

    private fun getDrawingByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
