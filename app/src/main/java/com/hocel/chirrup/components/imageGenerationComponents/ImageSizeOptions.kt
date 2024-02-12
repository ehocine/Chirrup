package com.hocel.chirrup.components.imageGenerationComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hocel.chirrup.ui.theme.SwitcherBackground
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.ui.theme.blue
import com.hocel.chirrup.utils.ImageSize
import com.hocel.chirrup.viewmodels.MainViewModel


@Composable
fun ImageSizeOptions(
    options: Array<ImageSize>,
    mainViewModel: MainViewModel,
    onSelectionChanged: (ImageSize) -> Unit
) {
    var selectedOption by remember { mutableStateOf(mainViewModel.imageSize) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(
                    size = 12.dp,
                )
            )
            .background(SwitcherBackground),
    ) {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .padding(
                        all = 4.dp,
                    )
            ) {
                Column(
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
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = option.name,
                        style = MaterialTheme.typography.body2,
                        color = if (option == selectedOption) Color.White else TextColor,
                    )
                    Text(
                        text = option.size,
                        style = MaterialTheme.typography.overline,
                        color = if (option == selectedOption) Color.White else TextColor,
                    )
                }
            }
        }
    }
}