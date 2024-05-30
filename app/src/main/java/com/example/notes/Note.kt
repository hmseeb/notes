package com.example.notes

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val isFavourite: Int,
    val location: String,
    val date: String,
    val drawing: ByteArray?
)