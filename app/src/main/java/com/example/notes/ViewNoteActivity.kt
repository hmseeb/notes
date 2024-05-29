package com.example.notes

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewNoteActivity : AppCompatActivity() {

    private lateinit var noteTitle: TextView
    private lateinit var noteContent: TextView
    private lateinit var noteLocation: TextView
    private lateinit var dbHelper: NotesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        noteLocation = findViewById(R.id.noteLocation)
        dbHelper = NotesDatabaseHelper(this)

        val noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            val note = dbHelper.getNoteById(noteId)
            noteTitle.text = note.title
            noteContent.text = note.content
            noteLocation.text = note.location
        }
    }
}
