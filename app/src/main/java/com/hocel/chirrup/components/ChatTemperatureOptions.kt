package com.hocel.chirrup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hocel.chirrup.ui.theme.SwitcherBackground
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.ui.theme.blue
import com.hocel.chirrup.utils.TemperatureData
import com.hocel.chirrup.viewmodels.MainViewModel

@Composable
fun ChatTemperatureOptions(
    options: Array<TemperatureData>,
    mainViewModel: MainViewModel,
    onSelectionChanged: (TemperatureData) -> Unit
) {
    var selectedOption by remember { mutableStateOf(mainViewModel.temperature) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(
                    size = 12.dp,
                )
            )
            .background(MaterialTheme.colors.SwitcherBackground),
    ) {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .padding(
                        all = 4.dp,
                    )
            ) {
                Text(
                    text = option.tempName,
                    style = typography.subtitle2,
                    color = if (option == selectedOption) Color.White else MaterialTheme.colors.TextColor,
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                size = 12.dp,
                            )
                        )
                        .clickable {
                            selectedOption = option
                            onSelectionChanged(option)
                        }
                        .background(
                            if (option == selectedOption) {
                                blue
                            } else {
                                Color.Transparent
                            }
                        )
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        )
                )
            }
        }
    }
}