package com.hocel.chirrup.components.imageGenerationComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.ui.theme.blue


@Composable
fun HorizontalNumberPicker(
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    min: Int = 0,
    max: Int = 10,
    default: Int = min,
    onValueChange: (Int) -> Unit = {}
) {
    var number by remember { mutableStateOf(default) }

    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        PickerButton(
            size = height,
            arrowIcon = Icons.Default.ArrowBack,
            enabled = number > min,
            onClick = {
                if (number > min) number--
                onValueChange(number)
            }
        )
        Text(
            text = number.toString(),
            fontSize = (height.value / 2).sp,
            color = TextColor,
            fontWeight = FontWeight.W500,
            modifier = Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Max)
                .align(Alignment.CenterVertically)
        )
        PickerButton(
            size = height,
            arrowIcon = Icons.Default.ArrowForward,
            enabled = number < max,
            onClick = {
                if (number < max) number++
                onValueChange(number)
            }
        )
    }
}


@Composable
fun PickerButton(
    size: Dp = 45.dp,
    arrowIcon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (enabled) blue else Color.LightGray
    IconButton(
        enabled = enabled,
        onClick = {
            onClick()
        },
        modifier = Modifier
            .padding(8.dp)
            .background(backgroundColor, CircleShape)
            .clip(CircleShape)
            .size(size)
    ) {
        Icon(imageVector = arrowIcon, contentDescription = null, tint = Color.White)
    }
}