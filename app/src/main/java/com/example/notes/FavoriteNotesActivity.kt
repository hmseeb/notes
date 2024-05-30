package com.example.notes

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

class FavoriteNotesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var titleFavoriteNotes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_notes)

        titleFavoriteNotes = findViewById(R.id.titleFavoriteNotes)
        recyclerView = findViewById(R.id.recyclerView)
        dbHelper = NotesDatabaseHelper(this)
        notesAdapter = NotesAdapter(this, dbHelper.getFavoriteNotes().toMutableList(), dbHelper)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = notesAdapter
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.updateNotes(dbHelper.getFavoriteNotes().toMutableList())
    }
}