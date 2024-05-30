package com.example.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class ViewNoteActivity : AppCompatActivity() {

    private lateinit var noteLocation: TextView
    private lateinit var noteDate: TextView
    private lateinit var noteTitle: TextView
    private lateinit var noteContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        noteLocation = findViewById(R.id.noteLocation)
        noteDate = findViewById(R.id.noteDate)
        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)

        val noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            val dbHelper = NotesDatabaseHelper(this)
            val note = dbHelper.getNoteByID(noteId)

            noteLocation.text = note.location
            noteDate.text = note.date
            noteTitle.text = note.title
            noteContent.text = note.content
        }
    }
}