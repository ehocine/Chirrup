package com.hocel.chirrup.views.forgotPassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hocel.chirrup.R
import com.hocel.chirrup.components.LottieAnim
import com.hocel.chirrup.navigation.Screens
import com.hocel.chirrup.ui.theme.BackgroundColor
import com.hocel.chirrup.ui.theme.ButtonColor
import com.hocel.chirrup.utils.Constants.loadingState
import com.hocel.chirrup.utils.LoadingState
import com.hocel.chirrup.utils.resetUserPassword
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ForgotPassword(navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var emailValue by remember { mutableStateOf("") }

    loadingState = MutableStateFlow(LoadingState.IDLE)
    val state by loadingState.collectAsState()

    Surface(
        modifier = Modifier.clickable(
            interactionSource = MutableInteractionSource(),
            indication = null
        ) { focusManager.clearFocus() },
        color = BackgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.retrieve_your_password),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.h5.fontSize
                    )

                    Spacer(modifier = Modifier.padding(10.dp))
                    LottieAnim(
                        modifier = Modifier.size(200.dp),
                        lottie = R.raw.forgot_password
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        OutlinedTextField(
                            value = emailValue,
                            onValueChange = { emailValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.email_address),
                                    color = ButtonColor
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.email_address),
                                    color = ButtonColor
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = ButtonColor
                            )
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                resetUserPassword(context, emailValue)
                            },
                            enabled = state != LoadingState.LOADING,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(ButtonColor)
                        ) {
                            if (state == LoadingState.LOADING) {
                                CircularProgressIndicator(color = Color.White)
                            } else {
                                Text(
                                    text = stringResource(R.string.submit),
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(
                            text = stringResource(R.string.back_to_login),
                            fontSize = MaterialTheme.typography.subtitle2.fontSize,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate(route = Screens.Login.route) {
                                    // popUpTo = navController.graph.startDestination
                                    launchSingleTop = true
                                }
                            })
                    }
                }
            }
        }
    }
}