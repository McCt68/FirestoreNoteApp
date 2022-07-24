package eu.example.firestorenoteapp.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import eu.example.firestorenoteapp.models.Notes
import eu.example.firestorenoteapp.repository.StorageRepository

class DetailViewModel(
	// Get a reference to our storage repository
	private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {

	// set the initial State to an empty - Make it private ??
	var detailUiState by mutableStateOf(DetailUiState())
		private set // can only set the detailsUiState from within this class

	// Set hasUser to the result of hasUser method from repository
	private val hasUser: Boolean
		get() = repository.hasUser()

	private val user: FirebaseUser?
		get() = repository.user() // Might need function call ()

	// Events to update the detailUiState
	fun onColorChange(colorIndex: Int) {
		// Change the value of colorIndex to the value provided as an argument when we -
		// called this function.
		// We are copying the value of the new value to the old one. So the state is being updated -
		// by doing this. ( Bad explanation)
		detailUiState = detailUiState.copy(colorIndex = colorIndex)
	}

	// Title change event
	fun onTitleChange(title: String) {
		detailUiState = detailUiState.copy(title = title)
	}

	// Note change event
	fun onNoteChange(note: String) {
		detailUiState = detailUiState.copy(note = note)
	}

	// Event Function to add a note to Firebase -
	// Calling the repository.addNote function
	// Then change the detailsUiState to the new updated detailUiState with the values we provide -
	// When we call repository.addNote
	fun addNote() {
		// Check if the user is logged in, and call addNote if user is logged in
		if (hasUser) {
			repository.addNote(
				userId = user!!.uid,
				title = detailUiState.title,
				description = detailUiState.note,
				color = detailUiState.colorIndex,
				timeStamp = Timestamp.now()
			) {
				detailUiState = detailUiState.copy(noteAddedStatus = it)
			}
		}
	}

	// Event to edit a note ??
	fun setEditFields(note: Notes){
		detailUiState = detailUiState.copy(
			colorIndex = note.colorIndex,
			title = note.title,
			note = note.description
		)
	}

	// Event to get a note from firestore, by calling the getNote function in the repository
	fun getNote(noteId: String){
		repository.getNote(
			noteId = noteId,
			onError = {}
		){
			detailUiState = detailUiState.copy(selectedNote = it)
			detailUiState.selectedNote?.let {
					whatIsLet ->
				setEditFields(whatIsLet) }
		}
	}

	fun updateNote(
		noteId: String
	){
		repository.updateNote(
			title = detailUiState.title,
			note = detailUiState.note,
			noteId = noteId,
			color = detailUiState.colorIndex
		){ updatedNote ->
			detailUiState = detailUiState.copy(updateNoteStatus = updatedNote)
		}
	}

	//
	fun resetNoteAddedStatus(){
		detailUiState = detailUiState.copy(
			noteAddedStatus = false,
			updateNoteStatus = false)
	}

	// Reset the whole state back to its normal ( Initial value)
	fun resetState(){
		detailUiState = DetailUiState() // Create a new Object, and thereby reset the whole state
	}





}

// Data class for the UI state
data class DetailUiState(
	val colorIndex: Int = 0,
	val title: String = "",
	val note: String = "",
	val noteAddedStatus: Boolean = false,
	val updateNoteStatus: Boolean = false,
	val selectedNote: Notes? = null // represents the unique note we work on ??
)