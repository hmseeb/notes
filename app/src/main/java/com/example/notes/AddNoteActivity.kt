package com.example.notes

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var saveButton: FloatingActionButton
    private lateinit var dbHelper: NotesDatabaseHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        saveButton = findViewById(R.id.saveButton)
        dbHelper = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            val note = dbHelper.getNoteById(noteId)
            noteTitle.setText(note.title)
            noteContent.setText(note.content)
        }

        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = noteTitle.text.toString()
        val content = noteContent.text.toString()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        if (noteId == -1) {
            // Create new note
            val note = Note(
                id = 0,
                title = title,
                content = content,
                isFavourite = 0,
                location = "", // Placeholder, will be updated later
                date = date
            )
            dbHelper.insertNote(note)
        } else {
            // Update existing note
            val note = Note(
                id = noteId,
                title = title,
                content = content,
                isFavourite = 0,
                location = "", // Placeholder, will be updated later
                date = date
            )
            dbHelper.updateNote(note)
        }
        finish()
    }
}