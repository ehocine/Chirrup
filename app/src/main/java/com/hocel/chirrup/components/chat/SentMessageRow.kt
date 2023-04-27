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
import androidx.compose.ui.unit.sp
import com.hocel.chirrup.ui.theme.SentBubbleColor
import com.hocel.chirrup.ui.theme.TextColor


@Composable
fun SentMessageRow(
    text: String,
    messageTime: String
) {

    // Whole column that contains chat bubble and padding on start or end
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = 64.dp,
                end = 8.dp,
                top = 10.dp,
                bottom = 10.dp
            )
    ) {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = messageTime,
            style = MaterialTheme.typography.caption,
            fontSize = 13.sp,
            color = TextColor,
        )

        // This is chat bubble
        ChatBubbleConstraints(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp))
                .background(SentBubbleColor)
                .clickable { },
            content = {
                TextMessageInsideBubble(
                    text = text,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    ),
                    lineHeight = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.body1
                )
            }
        )
    }
}