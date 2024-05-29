package com.example.notes

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    var isFavourite: Int,
    val location: String,
    val date: String
)