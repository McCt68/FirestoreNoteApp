package eu.example.firestorenoteapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.example.firestorenoteapp.home.Home
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

@Composable
fun Navigation(
	navController: NavHostController = rememberNavController(),
	loginViewModel: LoginViewModel
) {
	NavHost(
		navController = navController,
		startDestination = LoginRoutes.SignIn.name
	) {
		composable(route = LoginRoutes.SignIn.name) {
			LoginScreen(
				onNavigateToHomePage = {
					navController.navigate(HomeRoutes.Home.name) {
						launchSingleTop =
							true // only 1 item of screen, to prevent we have multiply of the same
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
					navController.navigate(HomeRoutes.Home.name) {
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

		composable(route = HomeRoutes.Home.name) {
			Home()
		}
	}
}