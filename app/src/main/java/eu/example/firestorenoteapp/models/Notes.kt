package eu.example.firestorenoteapp.models

import com.google.firebase.Timestamp

// this is sorta a model for a note ??
// it describes parameters that each note can have
// There are default values for each parameter

data class Notes(
	val userId: String = "",
	val title: String = "",
	val description: String = "",
	val timestamp: Timestamp = Timestamp.now(), // get the actual time ( current ? )
	val colorIndex: Int = 0,
	val documentId: String = ""
)
