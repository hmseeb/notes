package com.example.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private val notesList: List<Note>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val noteTitle: TextView = view.findViewById(R.id.noteTitle)
        val noteContent: TextView = view.findViewById(R.id.noteContent)
        val noteLocation: TextView = view.findViewById(R.id.noteLocation)
        val menuButton: ImageView = view.findViewById(R.id.menuButton)

        init {
            view.setOnClickListener(this)
            menuButton.setOnClickListener {
                showPopupMenu(it, adapterPosition)
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        private fun showPopupMenu(view: View, position: Int) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.note_menu)

            val note = notesList[position]
            if (note.isFavourite == 1) {
                popupMenu.menu.findItem(R.id.action_favorite).title = "Remove from Favorites"
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_favorite -> {
                        listener.onFavoriteClick(position)
                        true
                    }
                    R.id.action_edit -> {
                        listener.onEditClick(position)
                        true
                    }
                    R.id.action_delete -> {
                        listener.onDeleteClick(position)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]
        holder.noteTitle.text = note.title
        holder.noteContent.text = note.content
        holder.noteLocation.text = note.location
    }

    override fun getItemCount() = notesList.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onFavoriteClick(position: Int)
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }
}
