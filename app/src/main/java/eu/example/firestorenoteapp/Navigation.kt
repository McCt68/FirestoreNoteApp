package eu.example.firestorenoteapp

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.example.firestorenoteapp.detail.DetailScreen
import eu.example.firestorenoteapp.detail.DetailViewModel
import eu.example.firestorenoteapp.home.Home
import eu.example.firestorenoteapp.home.HomeViewModel
import eu.example.firestorenoteapp.login.LoginScreen
import eu.example.firestorenoteapp.login.LoginViewModel
import eu.example.firestorenoteapp.login.SignUpScreenScreen

// enum Class for LoginRoutes
enum class LoginRoutes {
	SignUp,
	SignIn
}

enum class HomeRoutes {
	Home,
	Detail
}

enum class NestedRoutes {
	Main,
	Login
}

@Composable
fun Navigation(
	navController: NavHostController = rememberNavController(),
	loginViewModel: LoginViewModel,
	detailViewModel: DetailViewModel,
	homeViewModel: HomeViewModel
) {
	NavHost(
		navController = navController,
		startDestination = NestedRoutes.Main.name
	) {
		authGraph(
			navController = navController,
			loginViewModel = loginViewModel)

		homeGraph(
			navController = navController,
			detailViewModel = detailViewModel,
			homeViewModel = homeViewModel
		)
	}
}

// Extension function for authGraph
// Adding a function to class NavGraphBuilder
fun NavGraphBuilder.authGraph(
	navController: NavHostController,
	loginViewModel: LoginViewModel,
) {
	navigation(
		startDestination = LoginRoutes.SignIn.name,
		route = NestedRoutes.Login.name
	) {
		composable(route = LoginRoutes.SignIn.name) {
			LoginScreen(
				onNavigateToHomePage = {
					navController.navigate(NestedRoutes.Main.name) {
						// Navigate to the "search” destination only if we’re not already on
						// the "search" destination, avoiding multiple copies on the top of the back stack
						launchSingleTop = true

						popUpTo(route = LoginRoutes.SignIn.name) {
							inclusive = true // quit the application, instead of navigating back ??
						}
					}

				},
				loginViewModel = loginViewModel
			) {
				navController.navigate(LoginRoutes.SignUp.name) {
					launchSingleTop = true
					popUpTo(LoginRoutes.SignIn.name) {
						inclusive = true
					}
				}
			}
		}

		composable(route = LoginRoutes.SignUp.name) {
			SignUpScreenScreen(
				onNavigateToHomePage = {
					navController.navigate(NestedRoutes.Main.name) {
						popUpTo(LoginRoutes.SignUp.name) {
							inclusive = true
						}
					}
				},
				loginViewModel = loginViewModel
			) {
				navController.navigate(LoginRoutes.SignIn.name)

			}
		}
	}
}

fun NavGraphBuilder.homeGraph(
	navController: NavHostController,
	detailViewModel: DetailViewModel,
	homeViewModel: HomeViewModel
) {
	navigation(
		startDestination = HomeRoutes.Home.name,
		route = NestedRoutes.Main.name
	) {
		composable(HomeRoutes.Home.name) {
			Home(homeViewModel = homeViewModel,
				onNoteClick = { noteId ->
					// pass arguments
					navController.navigate(
						HomeRoutes.Detail.name + "?id = $noteId"
					) {
						launchSingleTop =
							true // can only have one instance of this, on the backstack
					}
				},
				navToDetailPage = {
					navController.navigate(HomeRoutes.Detail.name)
				}
			) {
				navController.navigate(NestedRoutes.Login.name) {
					launchSingleTop = true
					popUpTo(0) {
						inclusive = true
					}
				}

			}
		}

		composable(
			route = HomeRoutes.Detail.name + "?id={}",
			arguments = listOf(navArgument("id") {
				type = NavType.StringType
				defaultValue = ""
			})
		) { entry ->
			DetailScreen(
				detailViewModel = detailViewModel,
				noteId = entry.arguments?.getString("id") as String,
			) {
				navController.navigateUp() // navigate up the stack to where we where
			}

		}
	}
}

