package com.example.notes

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class ViewNoteActivity : AppCompatActivity() {

    private lateinit var noteTitle: TextView
    private lateinit var noteContent: TextView
    private lateinit var noteLocation: TextView
    private lateinit var noteDate: TextView
    private lateinit var noteDrawing: ImageView
    private lateinit var dbHelper: NotesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        noteLocation = findViewById(R.id.noteLocation)
        noteDate = findViewById(R.id.noteDate)
        noteDrawing = findViewById(R.id.noteDrawing)
        dbHelper = NotesDatabaseHelper(this)

        val noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            val note = dbHelper.getNoteByID(noteId)
            noteTitle.text = note.title
            noteContent.text = note.content
            noteLocation.text = note.location
            noteDate.text = formatDate(note.date)

            // Display drawing if it exists
            note.drawing?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                noteDrawing.setImageBitmap(bitmap)
            }
        }
    }

    private fun formatDate(date: String): String {
        // Assume the date format stored is "yyyy-MM-dd HH:mm:ss"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()) // iOS style
        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate)
    }
}