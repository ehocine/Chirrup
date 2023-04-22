package com.hocel.chirrup.views.splash_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.hocel.chirrup.navigation.Screens
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.utils.LoadingState
import com.hocel.chirrup.utils.userLoggedIn
import com.hocel.chirrup.viewmodels.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val gettingApiKeyState by mainViewModel.gettingApiKeyState.collectAsState()
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000)
    )
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500L)
        mainViewModel.gettingOpenaiAPIKey(context)
    }
    when (gettingApiKeyState) {
        LoadingState.LOADED -> {
            navController.navigate(if (!userLoggedIn()) Screens.Login.route else Screens.HomeScreen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }
    }
    SplashContent(alpha = alphaAnim.value)
}

@Composable
fun SplashContent(alpha: Float) {

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.qloga_logo),
//                modifier = Modifier.alpha(alpha),
//                contentDescription = ""
//            )
            CircularProgressIndicator(
                color = MaterialTheme.colors.TextColor,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}