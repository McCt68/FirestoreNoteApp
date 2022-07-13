package eu.example.firestorenoteapp.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {

	// instantiate a firebase auth object
	// returns the current user
	val currentUser: FirebaseUser? = Firebase.auth.currentUser

	// Check if user is already logged in or not
	// Maybe change functionName to hasUserLoggedIn
	fun hasUser(): Boolean = Firebase.auth.currentUser != null

	// get the current user id (uid)
	fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

	// Create user. By using .await we don't block the main thread ??
	suspend fun createUser(
		email: String,
		password: String,
		onComplete: (Boolean) -> Unit
	) = withContext(Dispatchers.IO) {
		Firebase.auth
			.createUserWithEmailAndPassword(email, password)
			.addOnCompleteListener {
				if (it.isSuccessful) {
					onComplete.invoke((true))
				} else {
					onComplete.invoke(false)
				}
			}.await()
	}

	// Login user. By using .await we ont block the main thread ??
	// renamed to loginUSer from login
	suspend fun loginUser(
		email: String,
		password: String,
		onComplete: (Boolean) -> Unit
	) = withContext(Dispatchers.IO) {
		Firebase.auth
			.signInWithEmailAndPassword(email, password)
			.addOnCompleteListener {
				if (it.isSuccessful) {
					onComplete.invoke((true))
				} else {
					onComplete.invoke(false)
				}
			}.await()
	}
}