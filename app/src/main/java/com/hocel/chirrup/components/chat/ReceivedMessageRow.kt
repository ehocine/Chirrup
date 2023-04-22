package com.hocel.chirrup.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hocel.chirrup.ui.theme.ReceivedBubbleColor
import com.hocel.chirrup.ui.theme.ReceivedTextColor
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.ui.theme.grayText

@Composable
fun ReceivedMessageRow(
    text: String,
    messageTime: String,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = 8.dp,
                end = 64.dp,
                top = 10.dp,
                bottom = 10.dp
            )
    ) {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = messageTime,
            style = MaterialTheme.typography.overline,
            color = MaterialTheme.colors.TextColor,
        )

        //ChatBubble
        ChatBubbleConstraints(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomEnd = 16.dp, topEnd = 16.dp, bottomStart = 16.dp))
                .background(MaterialTheme.colors.ReceivedBubbleColor)
                .clickable { },
            content = {
                TextMessageInsideBubble(
                    modifier = Modifier.padding(
                        start = 4.dp,
                        top = 4.dp,
                        end = 8.dp,
                        bottom = 4.dp
                    ),
                    text = text,
                    color = MaterialTheme.colors.ReceivedTextColor,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    messageStat = {
                    }
                )
            }
        )
    }
}

