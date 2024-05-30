package com.example.notes

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var searchView: SearchView
    private lateinit var mainLayout: View
    private lateinit var titleAllNotes: TextView
    private lateinit var buttonFavoriteNotes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.mainLayout) // Correct reference
        titleAllNotes = findViewById(R.id.titleAllNotes)
        buttonFavoriteNotes = findViewById(R.id.buttonFavoriteNotes)
        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)
        searchView = findViewById(R.id.searchView)

        dbHelper = NotesDatabaseHelper(this)
        notesAdapter = NotesAdapter(this, dbHelper.getAllNotes().toMutableList(), dbHelper)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = notesAdapter

        fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        buttonFavoriteNotes.setOnClickListener {
            val intent = Intent(this, FavoriteNotesActivity::class.java)
            startActivity(intent)
        }

        // Set FAB icon color to white
        val fabDrawable = DrawableCompat.wrap(fab.drawable)
        DrawableCompat.setTint(fabDrawable, Color.WHITE)
        fab.setImageDrawable(fabDrawable)

        // Set SearchView text color and hint color
        val searchText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(Color.WHITE)
        searchText.setHintTextColor(Color.LTGRAY)

        // Set SearchView icons to white
        setSearchViewIconColor(searchView, Color.WHITE)

        // Dismiss keyboard on touch outside
        mainLayout.setOnTouchListener(fun(_: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            return false
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchNotes(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchNotes(it) }
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.updateNotes(dbHelper.getAllNotes().toMutableList())
    }

    private fun searchNotes(query: String) {
        val filteredNotes = dbHelper.getAllNotes().filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true) ||
                    it.location.contains(query, ignoreCase = true)
        }.toMutableList()
        notesAdapter.updateNotes(filteredNotes)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mainLayout.windowToken, 0)
    }

    private fun setSearchViewIconColor(searchView: SearchView, color: Int) {
        // Get the search icon and close icon
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        val closeIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)

        // Set the icons to white
        searchIcon.setImageDrawable(getTintedDrawable(searchIcon.drawable, color))
        closeIcon.setImageDrawable(getTintedDrawable(closeIcon.drawable, color))
    }

    private fun getTintedDrawable(drawable: Drawable, color: Int): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        return wrappedDrawable
    }
}