package com.example.notes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import java.io.ByteArrayOutputStream
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity(), LocationListener {

    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var drawingView: DrawingView
    private lateinit var saveButton: Button
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var locationManager: LocationManager
    private var currentLocation: String = ""
    private var currentDate: String = ""
    private var isEditing = false
    private var noteId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteContent = findViewById(R.id.noteContent)
        drawingView = findViewById(R.id.drawingView)
        saveButton = findViewById(R.id.saveButton)
        dbHelper = NotesDatabaseHelper(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            isEditing = true
            loadNoteDetails(noteId)
        } else {
            checkLocationPermission()
            currentDate = getCurrentDate()
        }

        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun loadNoteDetails(noteId: Int) {
        val note = dbHelper.getNoteByID(noteId)
        noteTitle.setText(note.title)
        noteContent.setText(note.content)
        note.drawing?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            drawingView.loadDrawing(bitmap)
        }
        currentLocation = note.location
        currentDate = note.date
    }

    private fun saveNote() {
        val title = noteTitle.text.toString()
        val content = noteContent.text.toString()
        val drawing = drawingView.getDrawing()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val drawingBytes = drawing?.toByteArray()
        Log.d("AddNoteActivity", "Location to save: $currentLocation") // Add logging for location
        val note = Note(
            id = if (isEditing) noteId else 0,
            title = title,
            content = content,
            isFavourite = 0,
            location = currentLocation,
            date = currentDate,
            drawing = drawingBytes
        )

        if (isEditing) {
            dbHelper.updateNote(note)
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.insertNote(note)
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun getLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                currentLocation = address.getAddressLine(0)
                Log.d("AddNoteActivity", "Location fetched: $currentLocation")
            } else {
                currentLocation = "${location.latitude}, ${location.longitude}"
                Log.d("AddNoteActivity", "Fallback to lat/long: $currentLocation")
            }
        }
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}