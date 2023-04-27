package com.hocel.chirrup.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hocel.chirrup.data.repository.CompletionRepository
import com.hocel.chirrup.data.repository.DataStoreRepository
import com.hocel.chirrup.models.ChatMessage
import com.hocel.chirrup.models.chatRequestBody.ChatRequestBody
import com.hocel.chirrup.models.chatRequestBody.Message
import com.hocel.chirrup.models.conversation.Conversation
import com.hocel.chirrup.models.user.User
import com.hocel.chirrup.navigation.Screens
import com.hocel.chirrup.utils.*
import com.hocel.chirrup.utils.Constants.FIRESTORE_USERS_DATABASE
import com.hocel.chirrup.utils.Constants.LIST_OF_CONVERSATIONS
import com.hocel.chirrup.utils.Constants.MESSAGES_LIMIT
import com.hocel.chirrup.utils.Constants.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val completionRepository: CompletionRepository,
    private val appInterceptor: AppInterceptor,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val readChatGptData = dataStoreRepository.readChatGptData
    private var _userInfo: MutableStateFlow<User> = MutableStateFlow(User())
    var userInfo = _userInfo.asStateFlow()

    private var _gettingApiKeyState = MutableStateFlow(LoadingState.IDLE)
    var gettingApiKeyState: StateFlow<LoadingState> = _gettingApiKeyState

    private var _gettingUserDataState = MutableStateFlow(LoadingState.IDLE)
    var gettingUserDataState: StateFlow<LoadingState> = _gettingUserDataState

    var messagesResponse: List<ChatMessage> by mutableStateOf(listOf())
        private set

    var messagesOfUser: List<Message> by mutableStateOf(listOf())
        private set

    private var _generatingResponseState = MutableStateFlow(LoadingState.IDLE)
    val generatingResponseState: StateFlow<LoadingState> = _generatingResponseState

    private var _saveOrDeleteState = MutableStateFlow(LoadingState.IDLE)
    val saveOrDeleteState: StateFlow<LoadingState> = _saveOrDeleteState

    private var _settingPreviousConversationState = MutableStateFlow(LoadingState.IDLE)
    val settingPreviousConversationState: StateFlow<LoadingState> =
        _settingPreviousConversationState

    private var openaiAPIKey: String = ""

    var action: ConversationHandlerAction by mutableStateOf(ConversationHandlerAction.ADD)
        private set

    private var previousConversation: Conversation by mutableStateOf(Conversation())

    var sheetStateContent: MutableStateFlow<ChatSheetStateContent> =
        MutableStateFlow(ChatSheetStateContent.Save)
        private set

    var model = ChatModels.GPT35Turbo.model
        private set

    var temperature = TemperatureData.BALANCED
        private set

    init {
        viewModelScope.launch {
            readChatGptData.collect { chatData ->
                model = chatData.model
                temperature =
                    TemperatureData.values().first { chatData.temperature == it.tempValue }
            }
        }
    }

    fun saveChatGptData() {
        viewModelScope.launch {
            dataStoreRepository.saveChatGptData(
                model = model,
                temperature = temperature.tempValue
            )
        }
    }

    // Getting openaiAPIKey from Firebase
    fun gettingOpenaiAPIKey(
        context: Context,
    ) {
        val db = Firebase.firestore
        val data = db.collection(Constants.FIRESTORE_PARAMETERS_DATABASE)
            .document(Constants.FIRESTORE_PARAMETERS_DOCUMENT)
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    _gettingApiKeyState.emit(LoadingState.LOADING)
                    data.addSnapshotListener { value, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        if (value != null && value.exists()) {
                            openaiAPIKey = value.getString(Constants.API_KEY)!!
                            appInterceptor.setOpenaiAPIKey(openaiAPIKey)
                        } else {
                            "An error occurred".toast(context, Toast.LENGTH_SHORT)
                        }
                    }
                    _gettingApiKeyState.emit(LoadingState.LOADED)
                } catch (e: Exception) {
                    _gettingApiKeyState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        "Something went wrong".toast(context, Toast.LENGTH_SHORT)
                    }
                }
            }
        } else {
            "Device is not connected to the internet".toast(context, Toast.LENGTH_SHORT)
        }
    }

    fun getUserInfo(context: Context) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
        if (hasInternetConnection(context)) {
            if (currentUser != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        _gettingUserDataState.emit(LoadingState.LOADING)
                        data?.addSnapshotListener { value, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }
                            if (value != null && value.exists()) {
                                _userInfo.value = value.toObject(User::class.java) ?: User()
                            } else {
                                "An error occurred".toast(context, Toast.LENGTH_SHORT)
                            }
                        }
                        _gettingUserDataState.emit(LoadingState.LOADED)
                    } catch (e: Exception) {
                        _gettingUserDataState.emit(LoadingState.ERROR)
                        withContext(Dispatchers.Main) {
                            "An error occurred".toast(context, Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
        } else {
            "Device is not connected to the internet".toast(context, Toast.LENGTH_SHORT)
        }
    }

    fun conversationHandler(
        context: Context,
        scope: CoroutineScope,
        action: ConversationHandlerAction,
        conversation: Conversation,
        onAddSuccess: () -> Unit,
        onRemoveSuccess: () -> Unit
    ) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    _saveOrDeleteState.emit(LoadingState.LOADING)
                    when (action) {
                        ConversationHandlerAction.ADD -> {
                            data?.update(
                                LIST_OF_CONVERSATIONS, FieldValue.arrayUnion(conversation)
                            )?.addOnSuccessListener {
                                onAddSuccess()
                                scope.launch {
                                    _saveOrDeleteState.emit(LoadingState.LOADED)
                                }
                            }?.addOnFailureListener {
                                scope.launch {
                                    _saveOrDeleteState.emit(LoadingState.ERROR)
                                }
                                "Something went wrong: $it".toast(context, Toast.LENGTH_SHORT)
                            }
                        }
                        ConversationHandlerAction.REMOVE -> {
                            data?.update(
                                LIST_OF_CONVERSATIONS, FieldValue.arrayRemove(conversation)
                            )?.addOnSuccessListener {
                                onRemoveSuccess()
                                scope.launch {
                                    _saveOrDeleteState.emit(LoadingState.LOADED)
                                }
                            }?.addOnFailureListener {
                                scope.launch {
                                    _saveOrDeleteState.emit(LoadingState.ERROR)
                                }
                                "Something went wrong: $it".toast(context, Toast.LENGTH_SHORT)
                            }
                        }
                        ConversationHandlerAction.UPDATE -> {
                            data?.update(
                                LIST_OF_CONVERSATIONS, FieldValue.arrayRemove(previousConversation)
                            )?.addOnSuccessListener {
                                data.update(
                                    LIST_OF_CONVERSATIONS, FieldValue.arrayUnion(conversation)
                                ).addOnSuccessListener {
                                    scope.launch {
                                        _saveOrDeleteState.emit(LoadingState.LOADED)
                                    }
                                    onAddSuccess()
                                }.addOnFailureListener {
                                    "Something went wrong: $it".toast(context, Toast.LENGTH_SHORT)
                                    scope.launch {
                                        _saveOrDeleteState.emit(LoadingState.ERROR)
                                    }
                                }
                            }?.addOnFailureListener {
                                scope.launch {
                                    _saveOrDeleteState.emit(LoadingState.ERROR)
                                }
                                "Something went wrong: $it".toast(context, Toast.LENGTH_SHORT)
                                Log.d("Error", it.toString())
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        "An error occurred".toast(context, Toast.LENGTH_SHORT)
                    }
                }
            }
        } else {
            "Device is not connected to the internet".toast(context, Toast.LENGTH_SHORT)
        }
    }

    fun setConversationAction(action: ConversationHandlerAction) {
        this.action = action
    }

    fun generateResponse(message: String) {
        viewModelScope.launch {
            try {
                _generatingResponseState.emit(LoadingState.LOADING)
                addMessageOfUser(message)
                val response = completionRepository.getChatResponse(
                    ChatRequestBody(
                        model = model,
                        messages = messagesOfUser,
                        temperature = temperature.tempValue
                    )
                )
                if (response.isSuccessful) {
                    addMessage(
                        message = ChatMessage(
                            message = response.body()!!.choices[0].message.content,
                            date = System.currentTimeMillis(),
                            messageFromBot = true
                        )
                    )
                    _generatingResponseState.emit(LoadingState.LOADED)
                    messagesLimitHandler(
                        currentMessagesLimit = _userInfo.value.messagesLimit,
                        action = MessageLimitHandlerAction.SUBSTRACT,
                        amount = 1
                    )
                } else {
                    _generatingResponseState.emit(LoadingState.ERROR)
                }
            } catch (e: Exception) {
                _generatingResponseState.emit(LoadingState.ERROR)
                e.printStackTrace()
            }
        }
    }

    fun addMessage(message: ChatMessage) {
        messagesResponse = messagesResponse + message
    }

    private fun addMessageOfUser(message: String) {
        messagesOfUser = messagesOfUser + Message(message, "user")
    }

    fun setGeneratingResponseState(state: LoadingState) {
        viewModelScope.launch {
            _generatingResponseState.emit(state)
        }
    }

    fun settingSelectedChat(conversation: Conversation) {
        viewModelScope.launch {
            _settingPreviousConversationState.emit(LoadingState.LOADING)

            // This is used too keep track of the previous conversation in order to update it
            previousConversation = conversation
            resetMessages()
            conversation.listOfUserMessages.forEach { message ->
                addMessageOfUser(message.content)
            }
            conversation.listOfMessagesConversation.forEach { chatMessage ->
                addMessage(chatMessage)
            }
            _settingPreviousConversationState.emit(LoadingState.LOADED)
        }
    }

    fun messagesLimitHandler(
        currentMessagesLimit: Int, action: MessageLimitHandlerAction, amount: Int
    ) {
        viewModelScope.launch {
            val db = Firebase.firestore
            val currentUser = Firebase.auth.currentUser
            val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
            try {
                when (action) {
                    MessageLimitHandlerAction.ADD -> {
                        data?.update(
                            MESSAGES_LIMIT, currentMessagesLimit + amount
                        )
                    }
                    MessageLimitHandlerAction.SUBSTRACT -> {
                        data?.update(
                            MESSAGES_LIMIT, currentMessagesLimit - amount
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetMessages() {
        messagesResponse = listOf()
        messagesOfUser = listOf()
    }

    fun setChatModel(selectedModel: String) {
        model = selectedModel
    }

    fun setChatTemperature(selectedTemperature: TemperatureData) {
        temperature = selectedTemperature
    }

    fun setChatSheetStateContent(sheetStateContent: ChatSheetStateContent) {
        this.sheetStateContent.value = sheetStateContent
    }

    fun signOut(
        context: Context, navController: NavController
    ) {
        try {
            auth.signOut()
            "Successfully signed out".toast(context, Toast.LENGTH_SHORT)
            navController.navigate(Screens.Login.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        } catch (e: Exception) {
            "An error occurred".toast(context, Toast.LENGTH_SHORT)
        }
    }
}