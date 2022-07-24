package eu.example.firestorenoteapp.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.example.firestorenoteapp.models.Notes
import eu.example.firestorenoteapp.repository.Resources
import eu.example.firestorenoteapp.repository.StorageRepository
import kotlinx.coroutines.launch

class HomeViewModel(
	// get reference to the repository and its fields
	private val repository: StorageRepository = StorageRepository()
) : ViewModel() {

	// Used to hold the state, its a mutable HomeUiState
	var homeUiState by mutableStateOf(HomeUiState())

	// Obtain reference to the user from repository -
	// I think I can not call it directly from the view, because the repository is set to private ??
	val user = repository.user()

	val hasUser: Boolean
		get() = repository.hasUser()

	private val userId: String
		get() = repository.getUserId()

	// event function to load notes
	fun loadNotes(){
		// Check is user is logged in
		if (hasUser){
			if (userId.isNotBlank()) {
				getUserNotes(userId)
			}

		}else{
			homeUiState = homeUiState.copy(notesList = Resources.Error(
				throwable = Throwable(message = "User is not logged in")
			))
		}
	}

	// Function to help us get the user notes
	private fun getUserNotes(userId: String) = viewModelScope.launch{
		repository.getUserNotes(userId).collect{
			homeUiState = homeUiState.copy(notesList = it)
		}
	}

	// delete a note
	fun deleteNote(noteId: String) = repository.deleteNote(noteId){
		homeUiState = homeUiState.copy(noteDeletedStatus = it)
	}

	// Sign out the user
	fun signOut() = repository.signOut()
}

data class HomeUiState(
	val notesList: Resources<List<Notes>> = Resources.Loading(),
	val noteDeletedStatus: Boolean = false
)