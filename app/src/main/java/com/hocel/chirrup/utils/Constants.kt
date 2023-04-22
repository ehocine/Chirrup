package com.hocel.chirrup.utils

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

object Constants {
    const val BASE_URL = "https://api.openai.com/"

    const val TIMEOUT_IN_MILLIS = 10000L

    var loadingState = MutableStateFlow(LoadingState.IDLE)

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    const val FIRESTORE_USERS_DATABASE = "users"

    const val LIST_OF_CONVERSATIONS = "conversations"

    const val FIRESTORE_PARAMETERS_DATABASE = "parameters"
    const val FIRESTORE_PARAMETERS_DOCUMENT = "parameters"

    const val MESSAGES_LIMIT = "messagesLimit"

    const val MESSAGES_REWARD = 5

    const val API_KEY = "apikey"
}