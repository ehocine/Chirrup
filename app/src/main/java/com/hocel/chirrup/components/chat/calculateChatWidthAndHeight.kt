package com.hocel.chirrup.components.chat

import androidx.compose.ui.layout.Placeable


fun calculateChatWidthAndHeight(
    chatRowData: ChatRowData,
    message: Placeable
) {
    chatRowData.rowWidth = message.width
    chatRowData.rowHeight = message.height

}