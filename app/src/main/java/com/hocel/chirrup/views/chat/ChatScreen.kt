package com.hocel.chirrup.views.chat

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Done
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
import com.hocel.chirrup.components.DisplayLoadingDialog
import com.hocel.chirrup.components.SaveConversationSheetContent
import com.hocel.chirrup.components.WatchAdDialog
import com.hocel.chirrup.components.chat.ChatInput
import com.hocel.chirrup.components.chat.ReceivedMessageRow
import com.hocel.chirrup.components.chat.SentMessageRow
import com.hocel.chirrup.models.ChatMessage
import com.hocel.chirrup.models.conversation.Conversation
import com.hocel.chirrup.ui.theme.*
import com.hocel.chirrup.utils.Constants.MESSAGES_REWARD
import com.hocel.chirrup.utils.LoadingState
import com.hocel.chirrup.utils.MessageLimitHandlerAction
import com.hocel.chirrup.utils.showRewardedAd
import com.hocel.chirrup.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

//TODO: review the processing dialog
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
    val saveModalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    var userNavigationUp by remember { mutableStateOf(false) }
    val saveOrDeleteState by mainViewModel.saveOrDeleteState.collectAsState()

    val user by mainViewModel.userInfo.collectAsState()

    val messagesLimit = user.messagesLimit

    var openLoadingDialog by remember { mutableStateOf(false) }
    var openWatchAdDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = messages.size) {
        scope.launch {
            scrollState.animateScrollToItem(messages.lastIndex)
        }
    }
    openLoadingDialog = when (saveOrDeleteState) {
        LoadingState.LOADING -> true
        else -> false
    }

    BackHandler {
        userNavigationUp = true
        if (messages.isNotEmpty()) {
            scope.launch {
                saveModalBottomSheetState.show()
            }
        }
    }

    ModalBottomSheetLayout(
        scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetState = saveModalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = MaterialTheme.colors.BottomSheetBackground,
        sheetContent = {
            SaveConversationSheetContent(
                onSaveYes = {
                    mainViewModel.conversationHandler(
                        context = context,
                        scope = scope,
                        action = mainViewModel.action,
                        conversation = Conversation(
                            listOfUserMessages = mainViewModel.messagesOfUser,
                            listOfMessagesConversation = mainViewModel.messagesResponse
                        ),
                        onAddSuccess = {
                            scope.launch {
                                saveModalBottomSheetState.hide()
                                navController.navigateUp()
                            }
                        },
                        onRemoveSuccess = {}
                    )
                }
            ) {
                scope.launch {
                    saveModalBottomSheetState.hide()
                    if (userNavigationUp) {
                        navController.navigateUp()
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chat", color = MaterialTheme.colors.TextColor) },
                    actions = {
                        IconButton(onClick = {
                            userNavigationUp = false
                            if (messages.isNotEmpty()) {
                                scope.launch {
                                    saveModalBottomSheetState.show()
                                }
                            }
                        }) {
                            Row() {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Save conversation",
                                    tint = MaterialTheme.colors.TextColor
                                )
                                Text(
                                    text = "Save conversation",
                                    color = MaterialTheme.colors.TextColor
                                )
                            }
                        }
                    },
                    backgroundColor = MaterialTheme.colors.BackgroundColor,
                    contentColor = MaterialTheme.colors.TextColor,
                    elevation = 0.dp,
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp, 24.dp)
                                .clickable {
                                    userNavigationUp = true
                                    if (messages.isNotEmpty()) {
                                        scope.launch {
                                            saveModalBottomSheetState.show()
                                        }
                                    } else {
                                        navController.navigateUp()
                                    }

                                },
                            tint = MaterialTheme.colors.TextColor
                        )
                    }
                )
            }
        ) {
            DisplayLoadingDialog(title = "Processing...", openDialog = openLoadingDialog)
            WatchAdDialog(
                title = "Watch an ad to earn more",
                message = {
                    Text(
                        text = "Watch an ad to earn $MESSAGES_REWARD more messages",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colors.TextColor,
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
                    .background(MaterialTheme.colors.BackgroundColor)
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
                        .background(if (messagesLimit <= 0) RedColor else MaterialTheme.colors.ButtonColor),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "You have $messagesLimit messages left",
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
                                color = MaterialTheme.colors.TextColor,
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
                                val sdf = remember {
                                    SimpleDateFormat("hh:mm", Locale.ROOT)
                                }
                                when (message.messageFromBot) {
                                    true -> {
                                        ReceivedMessageRow(
                                            text = message.message,
                                            messageTime = sdf.format(message.date)
                                        )
                                    }
                                    false -> {
                                        SentMessageRow(
                                            text = message.message,
                                            messageTime = sdf.format(message.date)
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
                                color = MaterialTheme.colors.TextColor
                            )
                        }
                    }
                    LoadingState.ERROR ->{
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Something went wrong, try again.",
                                style = MaterialTheme.typography.subtitle2,
                                color = MaterialTheme.colors.TextColor
                            )
                        }
                    }
                    else -> Unit
                }

                ChatInput(
                    generatingResponseState = generatingResponseState,
                    canSendMessage = messagesLimit > 0 || !user.hasMessagesLimit,
                    onMessageSend = { messageContent ->
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