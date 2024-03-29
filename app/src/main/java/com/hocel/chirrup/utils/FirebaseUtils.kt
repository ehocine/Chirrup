package com.hocel.chirrup.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hocel.chirrup.data.models.user.User
import com.hocel.chirrup.navigation.Screens
import com.hocel.chirrup.utils.Constants.FIRESTORE_USERS_DATABASE
import com.hocel.chirrup.utils.Constants.TIMEOUT_IN_MILLIS
import com.hocel.chirrup.utils.Constants.auth
import com.hocel.chirrup.utils.Constants.loadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

//Register new user
fun registerNewUser(
    navController: NavController,
    context: Context,
    userName: String,
    emailAddress: String,
    password: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.createUserWithEmailAndPassword(emailAddress, password).await()
                        loadingState.emit(LoadingState.LOADED)
                        withContext(Dispatchers.Main) {
                            val user = Firebase.auth.currentUser
                            val setUserName = userProfileChangeRequest {
                                displayName = userName
                            }
                            user!!.updateProfile(setUserName).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("Tag", user.displayName.toString())
                                    createUser(user)
                                }
                            }
                            user.sendEmailVerification().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    "Verification email sent".toast(context, Toast.LENGTH_SHORT)
                                }
                            }
                            navController.navigate(Screens.Login.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        "Time out: couldn't connect".toast(context, Toast.LENGTH_SHORT)
                    }
                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        Log.d("Tag", "Register: ${e.message}")
                    }
                }
            }
        } else {
            "Please verify your inputs".toast(context, Toast.LENGTH_SHORT)
        }
    } else {
        "Device is not connected to the internet".toast(context, Toast.LENGTH_SHORT)
    }
}

//Sign in existing user
fun signInUser(
    navController: NavController,
    context: Context,
    emailAddress: String,
    password: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.signInWithEmailAndPassword(emailAddress, password).await()
                        loadingState.emit(LoadingState.LOADED)
                        val user = Firebase.auth.currentUser
                        if (user!!.isEmailVerified) {
                            withContext(Dispatchers.Main) {
                                navController.navigate(Screens.HomeScreen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                "Your email address is not verified yet".toast(
                                    context,
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        "Time out: couldn't connect".toast(context, Toast.LENGTH_SHORT)
                    }
                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                }
            }
        } else {
            "Please verify your inputs".toast(context, Toast.LENGTH_SHORT)
        }
    } else {
        "Device is not connected to the internet".toast(context, Toast.LENGTH_SHORT)
    }
}


// Function to create a new user by getting the ID from the auth system
fun createUser(user: FirebaseUser?) {
    val db = Firebase.firestore
    val newUser = user?.let {
        User(
            userID = it.uid,
            name = it.displayName.toString(),
            email = it.email.toString(),
            conversations = listOf(),
            hasMessagesLimit = true,
            messagesLimit = 10
        )
    }
    if (newUser != null) {
        db.collection(FIRESTORE_USERS_DATABASE).document(user.uid)
            .set(newUser)
            .addOnCompleteListener { task ->
                Log.d("Tag", "success $task")
            }.addOnFailureListener { task ->
                Log.d("Tag", "Failure $task")
            }
    }
}

//Reset password function
fun resetUserPassword(
    context: Context,
    emailAddress: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.sendPasswordResetEmail(emailAddress).await()
                        loadingState.emit(LoadingState.LOADED)
                        withContext(Dispatchers.Main) {
                            "Email sent".toast(context, Toast.LENGTH_SHORT)
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        "Time out: couldn't connect".toast(context, Toast.LENGTH_SHORT)
                    }
                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        Log.d("Tag", "Reset: ${e.message}")
                    }
                }
            }
        } else {
            "Please verify your inputs".toast(context, Toast.LENGTH_SHORT)
        }
    } else {
        "Device is not connected to the internet".toast(context, Toast.LENGTH_SHORT)
    }
}


fun resendVerificationEmail(
    context: Context
) {
    val user = auth.currentUser
    if (user != null) {
        if (!user.isEmailVerified) {
            user.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    "Verification email sent".toast(context, Toast.LENGTH_SHORT)
                }
            }.addOnFailureListener {
            }
        } else {
            "Your email has already been verified".toast(context, Toast.LENGTH_SHORT)
        }
    } else {
        "An error occurred".toast(context, Toast.LENGTH_SHORT)
    }
}

// Function to check is the user is not null and has email verified
fun userLoggedIn(): Boolean {
    val user = Firebase.auth.currentUser
    return user != null && user.isEmailVerified
}