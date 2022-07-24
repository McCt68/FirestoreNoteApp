package eu.example.firestorenoteapp.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.example.firestorenoteapp.ui.theme.FirestoreNoteAppTheme

// Here is the view, its a loginScreen -
// Here I am observing the uiState, and can use values I observe from there to show in for example -
// Text, or handle ui Logic
// I should not modify the state directly from here, as I am only observing the state -
// If I need to modify the state, I should do that by calling methods in the viewModel -
// Then those methods inside the viewModel will modify the viewModel. state
// and i can use the new updated values by observing them from the view again
@Composable
fun LoginScreen(
	loginViewModel: LoginViewModel? = null, // pass null so we can use preview ??
	onNavigateToHomePage: () -> Unit,
	onNavigateToSignUpPage: () -> Unit,
) {

	// Create an instance of loginUi, to get the uiState
	val loginUiState = loginViewModel?.loginUiState
	val isError = loginUiState?.loginError != null
	val context = LocalContext.current

	Column(
		modifier = Modifier
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Login",
			style = MaterialTheme.typography.h3,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colors.primary
		)
		if (isError) {
			Text(
				text = loginUiState?.loginError ?: "Unknown error",
				color = Color.Red)
		}
		// Email Input
		OutlinedTextField(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth(),
			// pass in userName from viewModel, or set it to an empty string if it is null
			value = loginUiState?.userName ?: "",
			// This part I need a better understanding off -
			// I set the value to whatever I type in the textInput
			// I think this is is similar to a callback
			onValueChange = {loginViewModel?.onUserNameChange(it)},
		leadingIcon = {
			Icon(
				imageVector = Icons.Default.Person,
				contentDescription = null
			)},
		label = {
			Text(text = "Email")
		},
		isError = isError)

		// Password Input
		OutlinedTextField(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth(),
			// pass in userName from viewModel, or set it to an empty string if it is null
			value = loginUiState?.password ?: "",
			// This part I need a better understanding off -
			// I set the value to whatever I type in the textInput
			// I think this is is similar to a callback
			onValueChange = {loginViewModel?.onPasswordChange(it)}, // might not be the right call ??
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.Lock,
					contentDescription = null
				)},
			label = {
				Text(text = "Password")
			},
			visualTransformation = PasswordVisualTransformation(),
			isError = isError)
		
		//
		Button(onClick = { loginViewModel?.loginUser(context = context) }) {
			Text(text = "Sign In")
		}
		Spacer(modifier = Modifier.size(16.dp))

		// Sign up user
		Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
			Text(text = "Don't have an Account?")
			Spacer(modifier = Modifier.size(8.dp))
			TextButton(onClick = { onNavigateToSignUpPage.invoke() }) {
				Text(text = "SignUp")
			}
		}
		// show progressBar if loading
		// I think here we are actually observing it, the viewModel don't know that we are doing that ??
		if (loginUiState?.isLoading == true){
			CircularProgressIndicator()
		}
		
		LaunchedEffect(key1 = loginViewModel?.hasUser){
			if (loginViewModel?.hasUser == true){
				onNavigateToHomePage.invoke()
			}
		}

	}



}


//
@Composable
fun SignUpScreenScreen(
	loginViewModel: LoginViewModel? = null, // pass null so we can use preview ??
	onNavigateToHomePage: () -> Unit,
	onNavigateToLoginPage: () -> Unit,
) {

	// Create an instance of loginUi, to get the uiState
	val loginUiState = loginViewModel?.loginUiState
	val isError = loginUiState?.signUpError != null
	val context = LocalContext.current

	Column(
		modifier = Modifier
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Sign Up",
			style = MaterialTheme.typography.h3,
			fontWeight = FontWeight.Black,
			color = MaterialTheme.colors.primary
		)
		if (isError) {
			Text(text = loginUiState?.signUpError ?: "Unknown error", color = Color.Red)

		}
		// Email Input
		OutlinedTextField(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth(),
			// pass in userNameSignUp from viewModel, or set it to an empty string if it is null
			value = loginUiState?.userNameSignUp ?: "",
			// This part I need a better understanding off -
			// I set the value to whatever I type in the textInput
			// I think this is is similar to a callback
			onValueChange = {loginViewModel?.onUserNameSignupChange(it)}, // maybe wrong call ?
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.Person,
					contentDescription = null
				)},
			label = {
				Text(text = "Email")
			},
			isError = isError)

		// TRYING TO INSERT BLOCK HERE -----
		OutlinedTextField(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			value = loginUiState?.password ?: "", // changed from passWordSignUP
			onValueChange = { loginViewModel?.onPasswordSignupChange(it) },
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.Lock,
					contentDescription = null,
				)
			},
			label = {
				Text(text = "Password")
			},
			visualTransformation = PasswordVisualTransformation(),
			isError = isError
		)

		// BLOCK END !!!!

		// Password Input
		OutlinedTextField(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth(),
			// pass in userName from viewModel, or set it to an empty string if it is null
			value = loginUiState?.passwordSignUp ?: "", // RIGHT CALL ?
			// This part I need a better understanding off -
			// I set the value to whatever I type in the textInput
			// I think this is is similar to a callback
			onValueChange = {loginViewModel?.onConfirmPasswordChange(it)}, // might not be the right call ??
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.Lock,
					contentDescription = null
				)},
			label = {
				Text(text = "Confirm Password")
			},
			visualTransformation = PasswordVisualTransformation(),
			isError = isError)

		//
		Button(onClick = { loginViewModel?.createUser(context = context) }) {
			Text(text = "Sign In")
		}
		Spacer(modifier = Modifier.size(16.dp))

		// Sign up user
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.Center
		) {
			Text(text = "Already have an Account?")
			Spacer(modifier = Modifier.size(8.dp))
			// Invoke navigation
			TextButton(onClick = { onNavigateToLoginPage.invoke() }) {
				Text(text = "Sign In")
			}
		}
		// show progressBar if loading
		// I think here we are actually observing it, the viewModel don't know that we are doing that ??
		if (loginUiState?.isLoading == true){
			CircularProgressIndicator()
		}

		LaunchedEffect(key1 = loginViewModel?.hasUser){
			if (loginViewModel?.hasUser == true){
				onNavigateToHomePage.invoke()
			}
		}

	}



}

@Preview(showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
	FirestoreNoteAppTheme{
		LoginScreen(onNavigateToHomePage = { /*TODO*/ }) {
			
		}
	}
}

@Preview(showSystemUi = true)
@Composable
fun PreviewSignUpScreen() {
	FirestoreNoteAppTheme{
		SignUpScreenScreen(onNavigateToHomePage = { /*TODO*/ }) {
			
		}
	}
}