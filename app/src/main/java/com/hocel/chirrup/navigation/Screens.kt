package com.hocel.chirrup.navigation

sealed class Screens(val route: String){
    object SplashScreen: Screens(route = "splash_screen")
    object Login : Screens(route = "sign_in")
    object Register : Screens(route = "sign_up")
    object ForgotPassword : Screens(route = "forgot_password")
    object HomeScreen: Screens(route = "home_screen")
    object ChatScreen: Screens(route = "chat_screen")
}
