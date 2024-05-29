package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), NotesAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var adapter: NotesAdapter
    private lateinit var notesList: MutableList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)
        dbHelper = NotesDatabaseHelper(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        loadNotes()

        fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadNotes() {
        notesList = dbHelper.getAllNotes().toMutableList()
        adapter = NotesAdapter(notesList, this)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    override fun onItemClick(position: Int) {
        val note = notesList[position]
        val intent = Intent(this, ViewNoteActivity::class.java).apply {
            putExtra("NOTE_ID", note.id)
        }
        startActivity(intent)
    }

    override fun onFavoriteClick(position: Int) {
        val note = notesList[position]
        note.isFavourite = if (note.isFavourite == 1) 0 else 1
        dbHelper.updateNote(note)
        adapter.notifyItemChanged(position)
        Toast.makeText(this, if (note.isFavourite == 1) "Added to Favorites" else "Removed from Favorites", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClick(position: Int) {
        val intent = Intent(this, AddNoteActivity::class.java).apply {
            putExtra("NOTE_ID", notesList[position].id)
        }
        startActivity(intent)
    }

    override fun onDeleteClick(position: Int) {
        dbHelper.deleteNote(notesList[position].id)
        notesList.removeAt(position)
        adapter.notifyItemRemoved(position)
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })
        return true
    }

    private fun filterNotes(query: String?) {
        val filteredNotes = if (query.isNullOrEmpty()) {
            dbHelper.getAllNotes().toMutableList()
        } else {
            dbHelper.getAllNotes().filter {
                it.title.contains(query, true) || it.content.contains(query, true)
            }.toMutableList()
        }
        adapter = NotesAdapter(filteredNotes, this)
        recyclerView.adapter = adapter
    }
}
