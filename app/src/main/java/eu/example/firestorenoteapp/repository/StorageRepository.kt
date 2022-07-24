package eu.example.firestorenoteapp.repository

import android.provider.SyncStateContract.Helpers.update
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import eu.example.firestorenoteapp.models.Notes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val NOTES_COLLECTION_REF = "notes"

class StorageRepository() {

	fun user() = Firebase.auth.currentUser

	fun hasUser(): Boolean =
		Firebase.auth.currentUser != null // check is same as { return Fire....}

	// pass empty if the userId is null
	fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

	// make a var that refer to the notes collection in firestore
	private val notesRef: CollectionReference = Firebase
		.firestore.collection(NOTES_COLLECTION_REF)

	// fetch all the userNotes from firestore
	// I need a better understanding of this
	// When i call this function i supply a userID, and then it  returns via a flow a list of Notes for that userId
	@OptIn(ExperimentalCoroutinesApi::class)
	fun getUserNotes(
		userId: String
	): Flow<Resources<List<Notes>>> = callbackFlow {
		var snapshotStateListener: ListenerRegistration? = null

		// try and send a query to the firebase server to return the data to us ??
		try {
			snapshotStateListener = notesRef
				.orderBy("timestap")
				.whereEqualTo("userId", userId)
				.addSnapshotListener { snapshot, e ->
					val response = if (snapshot != null) {
						val notes = snapshot.toObjects(Notes::class.java)
						Resources.Success(data = notes)
					} else {
						Resources.Error(throwable = e?.cause) // get the error if there is one
					}
					trySend(response)
				}
		} catch (e: Exception) {
			trySend(Resources.Error(e?.cause))
			e.printStackTrace()
		}
		awaitClose {
			snapshotStateListener?.remove()
		}
	}

	// Get a single note from fireStore
	fun getNote(
		noteId: String,
		onError: (Throwable?) -> Unit,
		onSuccess: (Notes?) -> Unit
	) {
		// Using our collection reference, to get the nore of document(noteId)
		notesRef
			.document(noteId)
			.get()
			.addOnSuccessListener {
				onSuccess.invoke(it?.toObject(Notes::class.java)) // calling onSucces method, when we successfully retrive the document
			}
			.addOnFailureListener { result ->
				onError.invoke(result.cause)

			}
	}

	// Add a note to our firestore User with the id userId
	// The added note has values from the parameters passed when we call this function
	fun addNote(
		userId: String,
		title: String,
		description: String,
		timeStamp: Timestamp,
		color: Int = 0,
		onComplete: (Boolean) -> Unit
	) {
		// Generate fireBase document Id using our fireBase notes reference
		val documentId = notesRef.document().id // the id is generated automatically by firebase
		// set var note to the values provided from the function call
		val note = Notes(
			userId,
			title,
			description,
			timeStamp,
			colorIndex = color,
			documentId = documentId
		)
		// Update the added note in our firestore object we refer to (notesRef)
		notesRef
			.document(documentId)
			.set(note)
			.addOnCompleteListener { result ->
				onComplete.invoke(result.isSuccessful)
			}
	}

	// Delete a note in our firestore with our firestore reference object
	fun deleteNote(
		noteId: String,
		onComplete: (Boolean) -> Unit){
		notesRef
			.document(noteId)
			.delete()
			.addOnCompleteListener {
				onComplete.invoke(it.isSuccessful)
			}
	}

	// Update a note in our firestore collection by using our notesRef object
	fun updateNote(
		title: String,
		note: String,
		color: Int,
		noteId: String,
		onResult:(Boolean) -> Unit,
		){

		// Using a hasMap to create key - value pairs that represent the fields in each note
		// We create a hashMap where the key is of type String, and the value is of type Any.
		val updateData = hashMapOf<String, Any>(
			"colorIndex" to color,
			"description" to note,
			"title" to title
		)
		notesRef
			.document(noteId)
			.update(updateData) // update the data. We kinda convert the kotlin data to something firestore understands ?
			.addOnCompleteListener {
				onResult(it.isSuccessful)
			}
	}

	fun signOut() = Firebase.auth.signOut()
}

// Class to help us manage the state of getting the firestore data ??
// Sealed class of generic type
// constructor takes a data of type generic which is nullable
// and a throwable of type throwable which is going to be the error
sealed class Resources<T>(
	val data: T? = null,
	val throwable: Throwable? = null
) {
	class Loading<T> : Resources<T>() // Inherits from Resources
	class Success<T>(data: T?) : Resources<T>(data = data)
	class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}