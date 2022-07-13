package eu.example.firestorenoteapp.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.example.firestorenoteapp.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
	private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

	//
	val currentUser = repository.currentUser

	/*
	Basically, every time you get hasUser it queries repository.hasUser() so it is just a getter for that variable.
	And if you think the difference with val hasUser: Boolean = repository.hasUser() -
    it is that with custom getter the repository.hasUser() gets evaluated everytime you access hasUser, -
    however with the above example, the value gets evaluated at the initialization only
	 */
	val hasUser: Boolean
		get() = repository.hasUser()

	// This is holding the UiState, it can only be modified inside the viewModel since it is private
	// we are setting the initial value to the default values from creating a LoginUiState data class object ??
	// This is what we are observing from the view -
	// We are not directly modifying this from the view, but we can call methods in this viewModel -
	// from the view to modify this state, and then observe this state again
	var loginUiState by mutableStateOf(LoginUiState())
		private set

	// I think we are creating a copy of the object loginUiState. So we get a new instance where we change the parameter -
	// given as an argument with .copy(parameterName)
	// These are events that are modifying our state
	// when we call these functions from the view function -
	// Here we are not really observing the viewModel -
	// We are using methods from the viewModel in the view to update the uiState in the viewModel -
	// And then we are observing that uiState ??
	fun onUserNameChange(userName: String) {
		loginUiState = loginUiState.copy(userName = userName)
	}

	fun onPasswordChange(password: String) {
		loginUiState = loginUiState.copy(password = password)
	}

	fun onUserNameSignupChange(userName: String) {
		loginUiState = loginUiState.copy(userNameSignUp = userName)
	}

	fun onPasswordSignupChange(password: String) {
		loginUiState = loginUiState.copy(passwordSignUp = password)
	}

	fun onConfirmPasswordChange(password: String) {
		loginUiState = loginUiState.copy(confirmPasswordSignUp = password)
	}

	// Validate if user has entered input
	// Will it matter if i use a code block or = for the function ??
	private fun validateLoginForm() =
		loginUiState.userName.isNotBlank() &&
				loginUiState.password.isNotBlank()

	private fun validateSignUpForm() =
		loginUiState.userNameSignUp.isNotBlank() &&
				loginUiState.passwordSignUp.isNotBlank() &&
				loginUiState.confirmPasswordSignUp.isNotBlank()

	// Create user
	fun createUser(context: Context) = viewModelScope.launch {
		try {
			if (!validateSignUpForm()) {
				throw IllegalArgumentException("email and password can not be empty")
			}
			loginUiState = loginUiState.copy(isLoading = true)
			if (loginUiState.passwordSignUp != loginUiState.confirmPasswordSignUp) {
				throw IllegalArgumentException("the password is incorrect")
			}
			loginUiState = loginUiState.copy(signUpError = null) // it was already null or ??
			repository.createUser(
				email = loginUiState.userNameSignUp,
				password = loginUiState.passwordSignUp
			){isSuccessful ->
				if (isSuccessful){
					Toast.makeText(
						context,
						"Successful login",
						Toast.LENGTH_SHORT
					).show()
					loginUiState = loginUiState.copy(isSuccessLogin = true)
				}else{
					Toast.makeText(
						context,
						"Failed to login",
						Toast.LENGTH_SHORT
					).show()
					loginUiState = loginUiState.copy(isSuccessLogin = false)
				}
			}
		} catch (e: Exception) {
			loginUiState = loginUiState.copy(signUpError = e.localizedMessage)
			e.printStackTrace()
		}finally {
			loginUiState = loginUiState.copy(isLoading = false)
		}
	}

	// Login User
	fun loginUser(context: Context) = viewModelScope.launch {
		try {
			if (!validateLoginForm()) {
				throw IllegalArgumentException("Email and password can not be empty")
			}
			loginUiState = loginUiState.copy(isLoading = true)
			loginUiState = loginUiState.copy(loginError = null) // it was already null or ??
			repository.loginUser(
				email = loginUiState.userName,
				password = loginUiState.password
			){isSuccessful ->
				if (isSuccessful){
					Toast.makeText(
						context,
						"Successful login",
						Toast.LENGTH_SHORT
					).show()
					loginUiState = loginUiState.copy(isSuccessLogin = true)
				}else{
					Toast.makeText(
						context,
						"Failed to login",
						Toast.LENGTH_SHORT
					).show()
					loginUiState = loginUiState.copy(isSuccessLogin = false)
				}
			}
		} catch (e: Exception) {
			loginUiState = loginUiState.copy(loginError = e.localizedMessage)
			e.printStackTrace()
		}finally {
			loginUiState = loginUiState.copy(isLoading = false)
		}
	}


}


// I think this is the uiState we can pass to the view ??
// I need to understand this better
data class LoginUiState(
	val userName: String = "",
	val password: String = "",
	val userNameSignUp: String = "",
	val passwordSignUp: String = "",
	val confirmPasswordSignUp: String = "",
	val isLoading: Boolean = false,
	val isSuccessLogin: Boolean = false,
	val signUpError: String? = null,
	val loginError: String? = null
)