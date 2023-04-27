package com.hocel.chirrup.views.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.hocel.chirrup.R
import com.hocel.chirrup.components.*
import com.hocel.chirrup.models.conversation.Conversation
import com.hocel.chirrup.navigation.Screens
import com.hocel.chirrup.ui.theme.*
import com.hocel.chirrup.utils.Constants.MESSAGES_REWARD
import com.hocel.chirrup.utils.ConversationHandlerAction
import com.hocel.chirrup.utils.LoadingState
import com.hocel.chirrup.utils.MessageLimitHandlerAction
import com.hocel.chirrup.utils.showRewardedAd
import com.hocel.chirrup.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController, mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user by mainViewModel.userInfo.collectAsState()
    val gettingUserDataState by mainViewModel.gettingUserDataState.collectAsState()
    val deleteModalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var conversationToDelete by remember { mutableStateOf(Conversation()) }
    val saveOrDeleteState by mainViewModel.saveOrDeleteState.collectAsState()
    val settingPreviousConversationState by mainViewModel.settingPreviousConversationState.collectAsState()

    var openWatchAdDialog by remember { mutableStateOf(false) }

    val messagesLimit = user.messagesLimit

    ModalBottomSheetLayout(scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetState = deleteModalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = BottomSheetBackground,
        sheetContent = {
            DeleteConversationSheetContent(
                onDeleteYes = {
                    mainViewModel.conversationHandler(
                        context = context,
                        scope = scope,
                        action = ConversationHandlerAction.REMOVE,
                        conversation = conversationToDelete,
                        onAddSuccess = {},
                        onRemoveSuccess = {
                            scope.launch {
                                deleteModalBottomSheetState.hide()
                            }
                        })
                }
            ) {
                scope.launch {
                    deleteModalBottomSheetState.hide()
                }
            }
        }) {
        Scaffold(floatingActionButton = {
            ExtendedFloatingActionButton(text = {
                Text(
                    text = "Start a chat", color = Color.White
                )
            }, icon = {
                Icon(
                    imageVector = Icons.Default.Create,
                    tint = Color.White,
                    contentDescription = null
                )
            },
                backgroundColor = ButtonColor,
                onClick = {
                    mainViewModel.resetMessages()
                    mainViewModel.setConversationAction(ConversationHandlerAction.ADD)
                    navController.navigate(Screens.ChatScreen.route)
                }
            )
        }, bottomBar = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                // shows a banner ad
                AndroidView(
                    factory = { context ->
                        AdView(context).apply {
                            setAdSize(AdSize.BANNER)
                            adUnitId = context.getString(R.string.ad_id_banner)
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                )
            }
        }) {
            Surface(Modifier.fillMaxSize(), color = BackgroundColor) {
                DisplayLoadingDialog(
                    title = "Loading your chat...",
                    openDialog = settingPreviousConversationState == LoadingState.LOADING
                )
                DisplayLoadingDialog(
                    title = "Deleting your chat...",
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
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp)
                ) {
                    TopBar(
                        context = context,
                        mainViewModel = mainViewModel,
                        navController = navController
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (gettingUserDataState) {
                        LoadingState.LOADING -> LoadingList()
                        LoadingState.ERROR -> ErrorLoadingResults()
                        else -> {
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
                                            color = Color.White,
                                            textDecoration = TextDecoration.Underline,
                                            modifier = Modifier.clickable {
                                                openWatchAdDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                            if (user.conversations.isEmpty()) {
                                NoResults()
                            } else {
                                Text(
                                    text = "Here are your chats",
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.subtitle1,
                                    color = TextColor,
                                    modifier = Modifier.padding(16.dp)
                                )
                                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                    items(user.conversations) { conversation ->
                                        ConversationItem(
                                            conversation = conversation,
                                            onItemClicked = {
                                                mainViewModel.setConversationAction(
                                                    ConversationHandlerAction.UPDATE
                                                )
                                                mainViewModel.settingSelectedChat(it)
                                                navController.navigate(Screens.ChatScreen.route)
                                            },
                                            enableDeleteAction = true,
                                            onDeleteConversation = {
                                                conversationToDelete = it
                                                scope.launch {
                                                    deleteModalBottomSheetState.show()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}