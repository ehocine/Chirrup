package com.hocel.chirrup.components.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hocel.chirrup.ui.theme.ButtonColor
import com.hocel.chirrup.ui.theme.TextColor

@SuppressLint("UnrememberedMutableState")
@Composable
internal fun ChatInput(
    modifier: Modifier = Modifier,
    responseState: Boolean,
    canSendMessage: Boolean = true,
    onMessageSend: (String) -> Unit,
) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    val textEmpty: Boolean by derivedStateOf { input.text.isEmpty() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {

        TextField(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .weight(1f)
                .focusable(true),
            value = input,
            onValueChange = { input = it },
            enabled = responseState,
            textStyle = TextStyle(
                color = MaterialTheme.colors.TextColor,
                fontWeight = FontWeight.W500,
                fontSize = 16.sp
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = "Message")
            },
            trailingIcon = {
                IconButton(onClick = {
                    if (!textEmpty && canSendMessage) {
                        onMessageSend(input.text)
                        input = TextFieldValue("")
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colors.ButtonColor
                    )
                }
            }
        )
    }
}