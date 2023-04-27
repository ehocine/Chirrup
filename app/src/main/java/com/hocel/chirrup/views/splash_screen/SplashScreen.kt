package com.hocel.chirrup.views.splash_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.hocel.chirrup.navigation.Screens
import com.hocel.chirrup.ui.theme.BackgroundColor
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.utils.LoadingState
import com.hocel.chirrup.utils.userLoggedIn
import com.hocel.chirrup.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import com.hocel.chirrup.R

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

    Box(
        Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.chatbot),
                modifier = Modifier
                    .alpha(alpha)
                    .size(100.dp),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator(
                color = TextColor,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}