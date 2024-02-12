package com.hocel.chirrup.components.imageGenerationComponents

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hocel.chirrup.ui.theme.ButtonColor
import com.hocel.chirrup.ui.theme.RedColor
import com.hocel.chirrup.ui.theme.TextColor
import com.hocel.chirrup.utils.Constants.CREDIT
import com.hocel.chirrup.utils.ImageSize
import com.hocel.chirrup.utils.LoadingState
import com.hocel.chirrup.utils.toast
import com.hocel.chirrup.viewmodels.MainViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
internal fun PromptInput(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    mInput: String,
    state: LoadingState,
    responseState: Boolean,
    imageNumberLimit: Int,
    onMessageSend: (String) -> Unit,
) {
    var textAlertExpanded by remember { mutableStateOf(false) }
    var imagesNumber by remember { mutableStateOf(mainViewModel.numberOfImages) }
    var input by remember { mutableStateOf(TextFieldValue(mInput)) }
    val textEmpty: Boolean by derivedStateOf { input.text.isEmpty() }
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .focusable(true)
            .height(100.dp),
            value = input,
            onValueChange = { input = it },
            enabled = responseState,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ButtonColor
            ),
            placeholder = {
                Text(
                    text = "Write a description to generate images",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500
                )
            })
        Spacer(modifier = Modifier.height(15.dp))
        ImageSizeOptions(options = ImageSize.values(),
            mainViewModel = mainViewModel,
            onSelectionChanged = {
                mainViewModel.setImageSize(it)
            })
        Spacer(modifier = Modifier.height(15.dp))

        //Number of images to be generated
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = "Number of images",
                color = TextColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalNumberPicker(modifier = Modifier.fillMaxWidth(),
                min = 1,
                max = imageNumberLimit,
                default = imagesNumber,
                onValueChange = {
                    textAlertExpanded = true
                    imagesNumber = it
                    mainViewModel.setNumberOfImages(it)
                })
            AnimatedVisibility(visible = textAlertExpanded) {
                Text(
                    text = "$imagesNumber $CREDIT will be deduced.",
                    color = RedColor,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = {
                if (!textEmpty) {
                    if (imagesNumber <= imageNumberLimit) {
                        onMessageSend(input.text)
                        input = TextFieldValue("")
                    } else {
                        "You cannot request more than $imageNumberLimit image(s)".toast(
                            context,
                            Toast.LENGTH_SHORT
                        )
                    }
                } else {
                    "Prompt cannot be empty".toast(context, Toast.LENGTH_SHORT)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            enabled = imageNumberLimit > 0,
            colors = ButtonDefaults.buttonColors(ButtonColor)
        ) {
            if (state == LoadingState.LOADING) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text(
                    text = "Generate", fontSize = 16.sp, color = Color.White
                )
            }
        }
    }
}