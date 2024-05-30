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
import com.google.android.material.card.MaterialCardView

class NotesAdapter(
    private val context: Context,
    private var notesList: MutableList<Note>,
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

        // Set corner radius for first and last item
        val cardView = holder.itemView.findViewById<MaterialCardView>(R.id.noteCard)
        val radius = context.resources.getDimension(R.dimen.card_corner_radius)

        if (position == 0) {
            cardView.shapeAppearanceModel = cardView.shapeAppearanceModel
                .toBuilder()
                .setTopLeftCornerSize(radius)
                .setTopRightCornerSize(radius)
                .setBottomLeftCornerSize(0f)
                .setBottomRightCornerSize(0f)
                .build()
        } else if (position == itemCount - 1) {
            cardView.shapeAppearanceModel = cardView.shapeAppearanceModel
                .toBuilder()
                .setTopLeftCornerSize(0f)
                .setTopRightCornerSize(0f)
                .setBottomLeftCornerSize(radius)
                .setBottomRightCornerSize(radius)
                .build()
        } else {
            cardView.shapeAppearanceModel = cardView.shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(0f)
                .build()
        }

        // Hide the separator for the last item
        if (position == notesList.size - 1) {
            holder.separator.visibility = View.GONE
        } else {
            holder.separator.visibility = View.VISIBLE
        }

        // Set favorite icon
        val favoriteIcon = if (note.isFavourite == 1) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        holder.favoriteButton.setImageResource(favoriteIcon)
        holder.favoriteButton.setOnClickListener {
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
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun updateNotes(newNotes: List<Note>) {
        notesList = newNotes.toMutableList()
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
                else -> false
            }
        }
        popup.show()
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitle: TextView = itemView.findViewById(R.id.noteTitle)
        val noteContent: TextView = itemView.findViewById(R.id.noteContent)
        val menuButton: ImageView = itemView.findViewById(R.id.menuButton)
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteButton)
        val separator: View = itemView.findViewById(R.id.separator) // Added separator reference
    }
}