package com.hocel.chirrup.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hocel.chirrup.R
import com.hocel.chirrup.ui.theme.*


@Composable
fun LoadingList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
    }
}


@Composable
fun ErrorLoadingResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnim(modifier = Modifier.size(200.dp), lottie = R.raw.empty_state)
        Text(
            text = "Error loading data",
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 0.dp),
            color = MaterialTheme.colors.TextColor,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun NoResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnim(modifier = Modifier.size(200.dp), lottie = R.raw.empty_state)
        Text(
            text = "No entries",
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 0.dp),
            color = MaterialTheme.colors.TextColor,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun SignOutDropMenu(onSignOutClicked: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Menu",
            tint = MaterialTheme.colors.TextColor
        )
        DropdownMenu(
            modifier = Modifier.background(MaterialTheme.colors.CardColor),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = {
                expanded = false
                onSignOutClicked()
            }) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign out",
                        tint = MaterialTheme.colors.TextColor
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = "Sign out",
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colors.TextColor
                    )
                }
            }
        }
    }
}

@Composable
fun TransparentButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indication: Indication = rememberRipple(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        elevation = 0.dp,
        shape = shape,
        color = Color.Transparent,
        contentColor = Color.Transparent,
        border = null,
        modifier = modifier
            .then(
                Modifier
                    .clip(shape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = indication,
                        onClick = onClick
                    )
            ),
    ) {
        CompositionLocalProvider(LocalContentAlpha provides 1f) {
            ProvideTextStyle(
                value = MaterialTheme.typography.button
            ) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

@Composable
fun DeleteConversationSheetContent(
    onDeleteYes: () -> Unit,
    onDeleteCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.BackgroundColor)
    ) {
        Text(
            text = "Are you sure you want to delete?",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            color = MaterialTheme.colors.TextColor
        )
        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                onDeleteYes()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Yes",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colors.TextColor
            )
        }
        Divider(
            color = Color.Black.copy(alpha = 0.4f),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                onDeleteCancel()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Cancel",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colors.TextColor
            )
        }
    }
}

@Composable
fun SaveConversationSheetContent(
    onSaveYes: () -> Unit,
    onSaveNo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.BackgroundColor)
    ) {
        Text(
            text = "Do you want to save this conversation?",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            color = MaterialTheme.colors.TextColor
        )
        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                onSaveYes()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Yes",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colors.TextColor
            )
        }
        Divider(
            color = Color.Black.copy(alpha = 0.4f),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        TransparentButton(
            shape = RoundedCornerShape(0),
            onClick = {
                onSaveNo()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "No",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colors.TextColor
            )
        }
    }
}
@Composable
fun WatchAdDialog(
    title: String,
    message: @Composable (() -> Unit),
    openDialog: Boolean,
    closeDialog: () -> Unit,
    onYesClicked: () -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            title = {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.h6.fontSize,
                    fontWeight = FontWeight.Bold
                )
            },
            text = message,

            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.ButtonColor),
                    onClick = {
                        onYesClicked()
                        closeDialog()
                    })
                {
                    Text(text = "Yes", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { closeDialog() })
                {
                    Text(
                        text = "No",
                        color = MaterialTheme.colors.DialogNoText
                    )
                }
            },
            onDismissRequest = { closeDialog() }
        )
    }
}

@Composable
fun DisplayLoadingDialog(
    title: String,
    openDialog: Boolean
) {
    if (openDialog) {
        AlertDialog(
            title = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Title(title = title)
                }
            },
            text = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                }
            },
            buttons = {},
            onDismissRequest = {}
        )
    }
}