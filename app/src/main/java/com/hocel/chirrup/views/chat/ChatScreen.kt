package com.hocel.chirrup.views.chat

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hocel.chirrup.components.*
import com.hocel.chirrup.components.chat.ChatInput
import com.hocel.chirrup.components.chat.ReceivedMessageRow
import com.hocel.chirrup.components.chat.SentMessageRow
import com.hocel.chirrup.models.ChatMessage
import com.hocel.chirrup.models.conversation.Conversation
import com.hocel.chirrup.ui.theme.*
import com.hocel.chirrup.utils.*
import com.hocel.chirrup.utils.Constants.MESSAGES_REWARD
import com.hocel.chirrup.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    keyboardController: SoftwareKeyboardController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val messages = mainViewModel.messagesResponse
    val scrollState = rememberLazyListState()

    val generatingResponseState by mainViewModel.generatingResponseState.collectAsState()
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    var userNavigationUp by remember { mutableStateOf(false) }
    val saveOrDeleteState by mainViewModel.saveOrDeleteState.collectAsState()

    val sheetStateContent by mainViewModel.sheetStateContent.collectAsState()

    val user by mainViewModel.userInfo.collectAsState()

    val messagesLimit = user.messagesLimit

    var openWatchAdDialog by remember { mutableStateOf(false) }
    var chatModified by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                scrollState.animateScrollToItem(messages.lastIndex)
            }
        }
    }

    BackHandler {
        mainViewModel.setChatSheetStateContent(ChatSheetStateContent.Save)
        userNavigationUp = true
        if (chatModified) {
            scope.launch {
                modalBottomSheetState.show()
            }
        } else {
            mainViewModel.setGeneratingResponseState(LoadingState.IDLE)
            navController.navigateUp()
        }
    }

    ModalBottomSheetLayout(
        scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetState = modalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = BottomSheetBackground,
        sheetContent = {
            when (sheetStateContent) {
                ChatSheetStateContent.Save -> {
                    SaveConversationSheetContent(
                        onSaveYes = {
                            scope.launch {
                                modalBottomSheetState.hide()
                            }
                            if (generatingResponseState == LoadingState.LOADED) {
                                mainViewModel.conversationHandler(
                                    context = context,
                                    scope = scope,
                                    action = mainViewModel.action,
                                    conversation = Conversation(
                                        listOfUserMessages = mainViewModel.messagesOfUser,
                                        listOfMessagesConversation = mainViewModel.messagesResponse
                                    ),
                                    onAddSuccess = {
                                        mainViewModel.setGeneratingResponseState(LoadingState.IDLE)
                                        navController.navigateUp()
                                    },
                                    onRemoveSuccess = {}
                                )
                            } else {
                                "Please wait for the chat complete".toast(
                                    context,
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                    ) {
                        scope.launch {
                            modalBottomSheetState.hide()
                            if (userNavigationUp) {
                                mainViewModel.setGeneratingResponseState(LoadingState.IDLE)
                                navController.navigateUp()
                            }
                        }
                    }
                }
                ChatSheetStateContent.Settings -> {
                    SettingsSheetContent(
                        mainViewModel = mainViewModel,
                        onSaveClicked = {
                            mainViewModel.saveChatGptData()
                            scope.launch {
                                modalBottomSheetState.hide()
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chat", color = TextColor) },
                    actions = {
                        ChatDropMenu(
                            onSaveClicked = {
                                mainViewModel.setChatSheetStateContent(ChatSheetStateContent.Save)
                                userNavigationUp = false
                                if (messages.isNotEmpty()) {
                                    if (chatModified) {
                                        scope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    } else {
                                        "Chat is already saved".toast(context, Toast.LENGTH_SHORT)
                                    }
                                } else {
                                    "Chat is empty".toast(context, Toast.LENGTH_SHORT)
                                }
                            },
                            onSettingsClicked = {
                                mainViewModel.setChatSheetStateContent(ChatSheetStateContent.Settings)
                                scope.launch {
                                    modalBottomSheetState.show()
                                }
                            })
                    },
                    backgroundColor = BackgroundColor,
                    contentColor = TextColor,
                    elevation = 0.dp,
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp, 24.dp)
                                .clickable {
                                    userNavigationUp = true
                                    if (chatModified) {
                                        scope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    } else {
                                        mainViewModel.setGeneratingResponseState(LoadingState.IDLE)
                                        navController.navigateUp()
                                    }
                                },
                            tint = TextColor
                        )
                    }
                )
            }
        ) {
            DisplayLoadingDialog(
                title = "Processing...",
                openDialog = saveOrDeleteState == LoadingState.LOADING
            )
            WatchAdDialog(
                title = "Watch an ad to earn more",
                message = {
                    Text(
                        text = "Watch an ad to earn $MESSAGES_REWARD more messages",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Normal,
                        color = TextColor,
                    )
                },
                openDialog = openWatchAdDialog,
                closeDialog = { openWatchAdDialog = false },
                onYesClicked = {
                    showRewardedAd(context = context, handleReward = {
                        mainViewModel.messagesLimitHandler(
                            currentMessagesLimit = user.messagesLimit,
                            action = MessageLimitHandlerAction.ADD,
                            amount = MESSAGES_REWARD
                        )
                    })
                }
            )
            Column(
                modifier = Modifier
                    .background(BackgroundColor)
                    .fillMaxSize()
                    .focusable()
                    .wrapContentHeight()
                    .imePadding()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { keyboardController.hide() })
                    }
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(if (messagesLimit <= 0) RedColor else ButtonColor),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "You have $messagesLimit message(s) left",
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                        )
                        if (messagesLimit <= 0) {
                            Text(
                                text = ", add more",
                                style = MaterialTheme.typography.subtitle1,
                                textDecoration = TextDecoration.Underline,
                                color = Color.White,
                                modifier = Modifier.clickable {
                                    openWatchAdDialog = true
                                }
                            )
                        }
                    }
                }
                when (messages.isEmpty()) {
                    true -> {
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Start by asking a question",
                                color = TextColor,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.body2,
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            state = scrollState
                        ) {
                            items(messages) { message ->
                                when (message.messageFromBot) {
                                    true -> {
                                        ReceivedMessageRow(
                                            text = message.message,
                                            messageTime = convertTimeStampToDateAndTime(message.date),
                                            onCopyClicked = {
                                                copyToClipboard(context = context, text = it)
                                            }
                                        )
                                    }
                                    false -> {
                                        SentMessageRow(
                                            text = message.message,
                                            messageTime = convertTimeStampToDateAndTime(message.date)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                when (generatingResponseState) {
                    LoadingState.LOADING -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Generating answer...",
                                style = MaterialTheme.typography.subtitle2,
                                color = TextColor
                            )
                        }
                    }
                    LoadingState.ERROR -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Something went wrong, try again.",
                                style = MaterialTheme.typography.subtitle2,
                                color = TextColor
                            )
                        }
                    }
                    else -> Unit
                }
                ChatInput(
                    responseState = generatingResponseState != LoadingState.LOADING,
                    canSendMessage = messagesLimit > 0 || !user.hasMessagesLimit,
                    onMessageSend = { messageContent ->
                        chatModified = true
                        mainViewModel.addMessage(
                            ChatMessage(
                                message = messageContent,
                                date = System.currentTimeMillis(),
                                messageFromBot = false
                            )
                        )
                        mainViewModel.generateResponse(messageContent)
                    }
                )
            }
        }
    }
}