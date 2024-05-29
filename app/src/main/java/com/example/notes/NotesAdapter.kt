package com.example.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private val context: Context,
    private var notesList: List<Note>,
    private val dbHelper: NotesDatabaseHelper
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]
        holder.noteTitle.text = note.title
        holder.noteContent.text = note.content
        holder.menuButton.setOnClickListener {
            showPopupMenu(holder.menuButton, note)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewNoteActivity::class.java)
            intent.putExtra("NOTE_ID", note.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun updateNotes(newNotes: List<Note>) {
        notesList = newNotes
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, note: Note) {
        val popup = PopupMenu(context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.note_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    val intent = Intent(context, AddNoteActivity::class.java)
                    intent.putExtra("NOTE_ID", note.id)
                    context.startActivity(intent)
                    true
                }
                R.id.action_delete -> {
                    dbHelper.deleteNote(note.id)
                    updateNotes(dbHelper.getAllNotes())
                    true
                }
                R.id.action_favorite -> {
                    val isFavorite = if (note.isFavourite == 1) 0 else 1
                    dbHelper.updateNote(
                        Note(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            isFavourite = isFavorite,
                            location = note.location,
                            date = note.date,
                            drawing = note.drawing
                        )
                    )
                    updateNotes(dbHelper.getAllNotes())
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitle: TextView = itemView.findViewById(R.id.noteTitle)
        val noteContent: TextView = itemView.findViewById(R.id.noteContent)
        val menuButton: ImageView = itemView.findViewById(R.id.menuButton)
    }
}