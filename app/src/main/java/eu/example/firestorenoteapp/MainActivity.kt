package eu.example.firestorenoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.example.firestorenoteapp.detail.DetailViewModel
import eu.example.firestorenoteapp.home.HomeViewModel
import eu.example.firestorenoteapp.login.LoginViewModel
import eu.example.firestorenoteapp.ui.theme.FirestoreNoteAppTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {

			val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
			val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)
			val detailViewModel = viewModel(modelClass = DetailViewModel::class.java)

			FirestoreNoteAppTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colors.background
				) {
					Navigation(
						loginViewModel = loginViewModel,
						detailViewModel = detailViewModel,
						homeViewModel = homeViewModel
					)
				}
			}
		}
	}
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	FirestoreNoteAppTheme {

	}
}