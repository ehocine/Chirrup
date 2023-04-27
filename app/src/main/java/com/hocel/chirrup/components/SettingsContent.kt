package com.hocel.chirrup.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.hocel.chirrup.ui.theme.BackgroundColor
import com.hocel.chirrup.ui.theme.ButtonColor
import com.hocel.chirrup.ui.theme.RedColor
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.utils.ChatModels
import com.hocel.chirrup.utils.TemperatureData
import com.hocel.chirrup.viewmodels.MainViewModel

@Composable
fun SettingsSheetContent(
    mainViewModel: MainViewModel,
    onSaveClicked: () -> Unit
) {
//    val temperature by remember { mutableStateOf(mainViewModel.temperature) }

    var textAlertExpanded by remember {
        mutableStateOf(
            mainViewModel.temperature == TemperatureData.CREATIVE
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.BackgroundColor)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Title(title = "Chat parameters")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Select a model",
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp),
            color = MaterialTheme.colors.TextColor,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropDownOptions(
            value = mainViewModel.model,
            optionsList = ChatModels.values(),
            onOptionSelected = {
                mainViewModel.setChatModel(it)
            })
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Set the conversation style",
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp),
            color = MaterialTheme.colors.TextColor,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ChatTemperatureOptions(
                options = TemperatureData.values(),
                mainViewModel = mainViewModel,
                onSelectionChanged = {
                    mainViewModel.setChatTemperature(it)
                    textAlertExpanded = when (mainViewModel.temperature) {
                        TemperatureData.CREATIVE -> true
                        else -> false
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(visible = textAlertExpanded) {
            Text(
                text = "Creative may cause requests to take more time.",
                modifier = Modifier
                    .padding(16.dp, 0.dp, 0.dp, 0.dp),
                color = RedColor,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = MaterialTheme.colors.ButtonColor,
                contentColor = Color.White
            ),
            onClick = {
                onSaveClicked()
            }) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Composable
fun DropDownOptions(
    value: String,
    optionsList: Array<ChatModels>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(value) }
    val angle: Float by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .onGloballyPositioned {
                parentSize = it.size
            }
            .background(MaterialTheme.colors.BackgroundColor)
            .height(56.dp)
            .clickable { expanded = true }
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(
                    alpha = ContentAlpha.disabled
                ),
                shape = MaterialTheme.shapes.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(weight = 8f)
                .padding(start = 10.dp),
        ) {
            Text(
                text = selectedOption,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Medium
            )
        }
        IconButton(
            modifier = Modifier
                .alpha(ContentAlpha.medium)
                .rotate(degrees = angle)
                .weight(weight = 1.5f),
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Drop Down Arrow"
            )
        }
        DropdownMenu(
            modifier = Modifier
                .width(with(LocalDensity.current) { parentSize.width.toDp() }),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            optionsList.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        selectedOption = option.model
                        onOptionSelected(option.model)
                    }
                ) {
                    Text(text = option.model)
                }
            }
        }
    }
}