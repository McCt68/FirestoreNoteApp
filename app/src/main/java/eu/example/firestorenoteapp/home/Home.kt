package eu.example.firestorenoteapp.home


import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import eu.example.firestorenoteapp.Utils
import eu.example.firestorenoteapp.models.Notes
import eu.example.firestorenoteapp.repository.Resources
import eu.example.firestorenoteapp.ui.theme.FirestoreNoteAppTheme
import java.util.*


@OptIn(ExperimentalFoundationApi::class) // Need for using GridCells
@Composable
fun Home(
	homeViewModel: HomeViewModel?,
	onNoteClick: (id: String) -> Unit,
	navToDetailPage: () -> Unit,
	navToLoginPage: () -> Unit
) {
	val homeUiState = homeViewModel?.homeUiState ?: HomeUiState()

	var openDialog by remember {
		mutableStateOf(false)
	}

	var selectedNote: Notes? by remember {
		mutableStateOf(null)
	}

	val scope = rememberCoroutineScope()

	val scaffoldState = rememberScaffoldState()
	
	LaunchedEffect(key1 = Unit){
		homeViewModel?.loadNotes()
	}

	Scaffold(
		scaffoldState = scaffoldState,
		floatingActionButton = {
			FloatingActionButton(onClick = { navToDetailPage.invoke() }) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = null
				)

			}
		},
		topBar = {
			TopAppBar(
				navigationIcon = {},
				actions = {
					IconButton(onClick = {
						homeViewModel?.signOut()
						navToLoginPage.invoke()
					}) {
						Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
					}
				},
				title = {
					Text(text = "Home")
				}
			)
		}
	) { padding ->
		Column(modifier = Modifier.padding(padding)) {
			when (homeUiState.notesList) {
				is Resources.Loading -> {
					CircularProgressIndicator(
						modifier = Modifier
							.fillMaxSize()
							.wrapContentSize(align = Alignment.Center)
					)
				}
				is Resources.Success -> {
					LazyVerticalGrid(
						cells = GridCells.Fixed(2),
						contentPadding = PaddingValues(16.dp)
					) {
						items(homeUiState.notesList.data ?: emptyList()
						){ note ->
							NoteItem(
								notes = note,
								onLongClick = {
									openDialog = true
									selectedNote = note
								},
							) {
								onNoteClick.invoke(note.documentId)
							}
						} // Might need something to set openDialog to false ??
					}
				}
				else -> {
					Text(
						text = homeUiState
							.notesList.throwable?.localizedMessage ?: "Unknown error",
						color = Color.Red
					)
				}
			}

		}

	}
	// NOT SURE IF THIS IS THE RIGHT PLACE ??
	LaunchedEffect(key1 = homeViewModel?.hasUser ){
		// Nav to Loginpage if the user is not already logged in
		if (homeViewModel?.hasUser == false){
			// execute the lambda we gave as an argument to the Home Function
			// And thereby navigate us to loginPage
			navToLoginPage.invoke()
		}
	}

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
	notes: Notes,
	onLongClick: () -> Unit, // delete item when we perform longclick
	onClick: () -> Unit // help us navigate when we click a noteItem
) {
	Card(
		modifier = Modifier
			.combinedClickable(
				onLongClick = { onLongClick.invoke() },
				onClick = { onClick.invoke() }
			)
			.padding(8.dp)
			.fillMaxSize(),
		backgroundColor = Utils.colors[notes.colorIndex]
	) {
		Column {
			Text(
				text = notes.title,
				style = MaterialTheme.typography.h6,
				fontWeight = FontWeight.Bold,
				maxLines = 1,
				overflow = TextOverflow.Clip,
				modifier = Modifier.padding(4.dp)
			)
			Spacer(modifier = Modifier.size(4.dp))

			//
			CompositionLocalProvider(
				LocalContentAlpha provides ContentAlpha.disabled
			) {
				Text(
					text = notes.description,
					style = MaterialTheme.typography.body1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.padding(4.dp),
					maxLines = 4
				)
			}

			Spacer(modifier = Modifier.padding(4.dp))

			CompositionLocalProvider(
				LocalContentAlpha provides ContentAlpha.disabled
			) {
				Text(
					// call our format date function to convert the firebase timestamp to a string
					text = formatDate(notes.timestamp),
					style = MaterialTheme.typography.body1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier
						.padding(4.dp)
						.align(Alignment.End),
					maxLines = 4
				)
			}
		}
	}

}

// Function to help us format the date
// takes a firebase timeStamp and returns a string
private fun formatDate(timestamp: Timestamp): String {
	val simpleDateFormat = SimpleDateFormat("MM-dd-yy hh:mm", Locale.getDefault())
	return simpleDateFormat.format(timestamp.toDate())
}

@Preview
@Composable
fun HomeScreenPreview() {
	FirestoreNoteAppTheme {
		Home(homeViewModel = null, onNoteClick = {}, navToDetailPage = { /*TODO*/ }) {

		}
	}

}