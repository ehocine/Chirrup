package com.hocel.chirrup.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hocel.chirrup.data.models.conversation.Conversation
import com.hocel.chirrup.ui.theme.CardColor
import com.hocel.chirrup.ui.theme.RedColor
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.ui.theme.blue
import com.hocel.chirrup.utils.convertTimeStampToDateAndTime
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun ConversationItem(
    conversation: Conversation,
    onItemClicked: (Conversation) -> Unit,
    enableDeleteAction: Boolean = false,
    onDeleteConversation: (Conversation) -> Unit
) {
    var tagText = "Chat"
    var tagColor = RedColor
    val delete = SwipeAction(
        onSwipe = {
            onDeleteConversation(conversation)
        },
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete icon",
                tint = Color.White
            )
        },
        background = RedColor
    )
    SwipeableActionsBox(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        swipeThreshold = 120.dp,
        endActions = if (enableDeleteAction) listOf(delete) else listOf()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = {
                    onItemClicked(conversation)
                }),
            elevation = 0.dp,
            backgroundColor = CardColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(8f)
                ) {
                    if (conversation.chatConversation.listOfMessagesConversation.isNotEmpty()) {
                        ChatContent(conversation)
                    } else {
                        tagText = "Image"
                        tagColor = blue
                        ImageGenContent(conversation)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f),
                    horizontalArrangement = Arrangement.End
                ) {
                    ChatTag(type = tagText, color = tagColor)
                }
            }
        }
    }
}

@Composable
private fun ChatContent(
    conversation: Conversation
) {
    Text(
        text = "You: ${conversation.chatConversation.listOfMessagesConversation[conversation.chatConversation.listOfMessagesConversation.lastIndex - 1].message}",
        color = TextColor,
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = "AI: ${conversation.chatConversation.listOfMessagesConversation[conversation.chatConversation.listOfMessagesConversation.lastIndex].message}",
        color = TextColor,
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(8.dp))
    Column {
        Text(
            text = "Chat last modified on",
            color = TextColor,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 4.dp)
                .wrapContentSize(Alignment.BottomStart)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                tint = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(
                    alpha = 0.7f
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = convertTimeStampToDateAndTime(conversation.chatConversation.listOfMessagesConversation[conversation.chatConversation.listOfMessagesConversation.lastIndex].date),
                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(
                    alpha = 0.7f
                ),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }

}

@Composable
private fun ImageGenContent(
    conversation: Conversation,
) {
    Text(
        text = "Prompt: ${conversation.imageGenerationData.prompt}",
        color = TextColor,
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(8.dp))
    Column {
        Text(
            text = "Images generated on",
            color = TextColor,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 4.dp)
                .wrapContentSize(Alignment.BottomStart)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                tint = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(
                    alpha = 0.7f
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = convertTimeStampToDateAndTime(conversation.imageGenerationData.requestResponse.created),
                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(
                    alpha = 0.7f
                ),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

