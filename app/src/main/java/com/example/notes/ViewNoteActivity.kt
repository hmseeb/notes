package com.example.notes

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class ViewNoteActivity : AppCompatActivity() {

    private lateinit var noteLocation: TextView
    private lateinit var noteDate: TextView
    private lateinit var noteTitle: TextView
    private lateinit var noteContent: TextView
    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        noteLocation = findViewById(R.id.noteLocation)
        noteDate = findViewById(R.id.noteDate)
        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        drawingView = findViewById(R.id.drawingView)

        val noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            val dbHelper = NotesDatabaseHelper(this)
            val note = dbHelper.getNoteByID(noteId)

            noteLocation.text = note.location
            noteDate.text = note.date
            noteTitle.text = note.title
            noteContent.text = note.content

            note.drawing?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                drawingView.loadDrawing(bitmap)
            }
            drawingView.setDrawingEnabled(false) // Ensure drawing is disabled in view mode
        }
    }
}