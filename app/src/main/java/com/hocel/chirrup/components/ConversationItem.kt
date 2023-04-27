package com.hocel.chirrup.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import com.hocel.chirrup.models.conversation.Conversation
import com.hocel.chirrup.ui.theme.CardColor
import com.hocel.chirrup.ui.theme.RedColor
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.utils.convertTimeStampToDateAndTime
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun ConversationItem(
    conversation: Conversation,
    onItemClicked: (conversation: Conversation) -> Unit,
    enableDeleteAction: Boolean = false,
    onDeleteConversation: (conversation: Conversation) -> Unit
) {
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
            backgroundColor = MaterialTheme.colors.CardColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 12.dp)
                        .align(Alignment.CenterVertically)
                        .weight(0.6f)
                ) {

                    Text(
                        text = conversation.listOfUserMessages[0].content,
                        color = MaterialTheme.colors.TextColor,
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text(
                            text = "CV created on",
                            color = MaterialTheme.colors.TextColor,
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
                                text = convertTimeStampToDateAndTime(conversation.listOfMessagesConversation[conversation.listOfMessagesConversation.lastIndex].date),
                                modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(
                                    alpha = 0.7f
                                ),
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
                    }
                }
            }
        }
    }
}