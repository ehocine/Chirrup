package com.hocel.chirrup.views.imageViewer

import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.hocel.chirrup.R
import com.hocel.chirrup.components.ChatDropMenu
import com.hocel.chirrup.components.DeleteConversationSheetContent
import com.hocel.chirrup.data.models.imageGeneration.apiResponse.Data
import com.hocel.chirrup.ui.theme.BackgroundColor
import com.hocel.chirrup.ui.theme.BottomSheetBackground
import com.hocel.chirrup.ui.theme.ButtonColor
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.utils.ConversationHandlerAction
import com.hocel.chirrup.utils.downloadFile
import com.hocel.chirrup.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ImageViewerScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    keyboardController: SoftwareKeyboardController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val deleteModalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val conversation = mainViewModel.previousConversation
    BackHandler {
        mainViewModel.setFromImageGenerationValue(false)
    }
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
                        conversation = conversation,
                        onAddSuccess = {},
                        onRemoveSuccess = {
                            scope.launch {
                                deleteModalBottomSheetState.hide()
                            }
                            navController.navigateUp()
                        })
                }
            ) {
                scope.launch {
                    deleteModalBottomSheetState.hide()
                }
            }
        }) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("View images", color = TextColor) },
                actions = {
                    if (!mainViewModel.fromImageGeneration.value) {
                        ChatDropMenu(title = "Delete", icon = Icons.Default.Delete) {
                            scope.launch {
                                deleteModalBottomSheetState.show()
                            }
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
                                mainViewModel.setFromImageGenerationValue(false)
                                navController.navigateUp()
                            },
                        tint = TextColor
                    )
                })
        }) { paddingValue ->
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
                Column(
                    modifier = Modifier
                        .padding(top = 5.dp, end = 10.dp, start = 10.dp, bottom = 5.dp)
                        .border(
                            width = 1.dp,
                            color = ButtonColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "Prompt :",
                        Modifier.padding(start = 10.dp, top = 10.dp),
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = mainViewModel.previousConversation.imageGenerationData.prompt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                        maxLines = 15
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                ImageSlider(
                    listOfUrls = conversation.imageGenerationData.requestResponse.data,
                    onSaveLocally = {
                        downloadFile(
                            mContext = context,
                            fileName = "${System.currentTimeMillis()}",
                            fileExtension = ".jpg",
                            destinationDirectory = Environment.DIRECTORY_DOWNLOADS,
                            uri = it.toUri()

                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ImageSlider(
    modifier: Modifier = Modifier,
    listOfUrls: List<Data>,
    onSaveLocally: (String) -> Unit
) {

    LazyRow(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        items(listOfUrls) { image ->
            ImageCard(imageUrl = image.url, onSaveLocally = {
                onSaveLocally(it)
            })
        }
    }
}

@Composable
private fun ImageCard(imageUrl: String, onSaveLocally: (String) -> Unit) {
    Box(
        Modifier
            .width(300.dp)
            .height(400.dp)
            .padding(top = 5.dp, end = 10.dp, start = 10.dp, bottom = 5.dp)
            .border(
                width = 1.dp,
                color = ButtonColor,
                shape = RoundedCornerShape(12.dp)
            )
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Card(
            elevation = 4.dp
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp)),
                alignment = Alignment.CenterStart,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .build(),
                contentDescription = "Image"
            ) {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TextColor)
                    }
                } else {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier.clip(RectangleShape),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .background(Color.Black.copy(alpha = 0.6f)), horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { onSaveLocally(imageUrl) }) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}