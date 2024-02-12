package com.hocel.chirrup.views.imageGeneration

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.hocel.chirrup.components.ChatDropMenu
import com.hocel.chirrup.components.SaveConversationSheetContent
import com.hocel.chirrup.components.WatchAdDialog
import com.hocel.chirrup.components.imageGenerationComponents.ImageItem
import com.hocel.chirrup.components.imageGenerationComponents.PromptInput
import com.hocel.chirrup.data.models.conversation.ChatConversation
import com.hocel.chirrup.data.models.conversation.Conversation
import com.hocel.chirrup.data.models.conversation.ImageGenerationData
import com.hocel.chirrup.navigation.Screens
import com.hocel.chirrup.ui.theme.BackgroundColor
import com.hocel.chirrup.ui.theme.BottomSheetBackground
import com.hocel.chirrup.ui.theme.ButtonColor
import com.hocel.chirrup.ui.theme.RedColor
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.utils.ChatSheetStateContent
import com.hocel.chirrup.utils.Constants.CREDIT
import com.hocel.chirrup.utils.Constants.MESSAGES_REWARD
import com.hocel.chirrup.utils.LoadingState
import com.hocel.chirrup.utils.MessageLimitHandlerAction
import com.hocel.chirrup.utils.showRewardedAd
import com.hocel.chirrup.utils.toast
import com.hocel.chirrup.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ImageGenerationScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    keyboardController: SoftwareKeyboardController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val generatingResponseState by mainViewModel.generatingResponseState.collectAsState()
    val scrollGridState = rememberLazyGridState()
    val responses = mainViewModel.requestImagesResponse
    val user by mainViewModel.userInfo.collectAsState()
    val creditsNumberLimit = user.messagesLimit
    var openWatchAdDialog by remember { mutableStateOf(false) }
    var userNavigationUp by remember { mutableStateOf(false) }
    var prompt by remember { mutableStateOf(mainViewModel.prompt.value) }
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var loadingImages by remember { mutableStateOf(false) }
    BackHandler {
        mainViewModel.setChatSheetStateContent(ChatSheetStateContent.Save)
        userNavigationUp = true
        if (mainViewModel.promptModified.value) {
            scope.launch {
                modalBottomSheetState.show()
            }
        } else {
            mainViewModel.setGeneratingResponseState(LoadingState.IDLE)
            mainViewModel.setPrompt("")
            navController.navigateUp()
        }
    }
    ModalBottomSheetLayout(scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetState = modalBottomSheetState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = BottomSheetBackground,
        sheetContent = {
            SaveConversationSheetContent(onSaveYes = {
                scope.launch {
                    modalBottomSheetState.hide()
                }
                if (generatingResponseState == LoadingState.LOADED) {
                    mainViewModel.conversationHandler(context = context,
                        scope = scope,
                        action = mainViewModel.action,
                        conversation = Conversation(
                            imageGenerationData = ImageGenerationData(
                                prompt = mainViewModel.imagesRequestBody.prompt,
                                requestResponse = mainViewModel.requestImagesResponse
                            )
                        ),
                        onAddSuccess = {
                            mainViewModel.setPromptModifiedValue(false)
                            mainViewModel.setGeneratingResponseState(LoadingState.IDLE)
                            "Saved successfully".toast(context, Toast.LENGTH_SHORT)
                            navController.navigateUp()
                        },
                        onRemoveSuccess = {})
                } else {
                    "Please wait for completion".toast(
                        context, Toast.LENGTH_SHORT
                    )
                }
            }) {
                scope.launch {
                    modalBottomSheetState.hide()
                    if (userNavigationUp) {
                        mainViewModel.setGeneratingResponseState(LoadingState.IDLE)
                        navController.navigateUp()
                    }
                }
            }
        }) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("Generate images", color = TextColor) },
                actions = {
                    ChatDropMenu(title = "Save", icon = Icons.Default.Done) {
                        if (responses.data.isNotEmpty()) {
                            if (mainViewModel.promptModified.value) {
                                scope.launch {
                                    modalBottomSheetState.show()
                                }
                            } else {
                                "Already saved".toast(context, Toast.LENGTH_SHORT)
                            }
                        } else {
                            "Nothing to save".toast(context, Toast.LENGTH_SHORT)
                        }
                    }
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
                                if (mainViewModel.promptModified.value) {
                                    scope.launch {
                                        modalBottomSheetState.show()
                                    }
                                } else {
                                    mainViewModel.setGeneratingResponseState(LoadingState.IDLE)
                                    mainViewModel.setPrompt("")
                                    navController.navigateUp()
                                }
                            },
                        tint = TextColor
                    )
                })
        }) { paddingValue ->
            WatchAdDialog(title = "Watch an ad to earn more",
                message = {
                    Text(
                        text = "Watch an ad to earn $MESSAGES_REWARD more $CREDIT",
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
                })
            Column(
                modifier = Modifier
                    .padding(top = paddingValue.calculateTopPadding())
                    .background(BackgroundColor)
                    .fillMaxSize()
                    .focusable()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { keyboardController.hide() })
                    }, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(if (creditsNumberLimit <= 0) RedColor else ButtonColor),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "You have $creditsNumberLimit $CREDIT left",
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                        )
                        if (creditsNumberLimit <= 0) {
                            Text(text = ", add more",
                                style = MaterialTheme.typography.subtitle1,
                                color = Color.White,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    openWatchAdDialog = true
                                })
                        }
                    }
                }
                PromptInput(
                    modifier = Modifier.fillMaxWidth(),
                    mainViewModel = mainViewModel,
                    mInput = prompt,
                    state = generatingResponseState,
                    responseState = generatingResponseState != LoadingState.LOADING,
                    imageNumberLimit = creditsNumberLimit
                ) {
                    mainViewModel.setPromptModifiedValue(true)
                    prompt = it
                    loadingImages = true
                    mainViewModel.generateImagesResponse(it)
                }
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(),
                    state = scrollGridState,
                    columns = GridCells.Adaptive(200.dp)
                ) {
                    if (loadingImages) {
                        items(responses.data) { data ->
                            ImageItem(url = data.url, onImageClicked = {
                                mainViewModel.settingSelectedChat(
                                    conversation = Conversation(
                                        chatConversation = ChatConversation(),
                                        imageGenerationData = ImageGenerationData(
                                            prompt = prompt,
                                            requestResponse = responses
                                        )
                                    )
                                )
                                mainViewModel.setFromImageGenerationValue(true)
                                navController.navigate(Screens.ImageViewerScreen.route)
                            })
                        }
                    }
                }
            }
        }
    }
}