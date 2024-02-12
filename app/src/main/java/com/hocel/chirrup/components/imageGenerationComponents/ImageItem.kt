package com.hocel.chirrup.components.imageGenerationComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.hocel.chirrup.ui.theme.blue

@Composable
fun ImageItem(url: String,onImageClicked: () -> Unit) {
    val sizeInDp = 200.dp
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .clickable {
                    onImageClicked()
                }
                .size(sizeInDp)
                .clip(RoundedCornerShape(15.dp))
                .padding(bottom = 10.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = "Image",
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading) {
                Box(
                    modifier = Modifier.size(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = blue)
                }
            } else {
                SubcomposeAsyncImageContent(
                    modifier = Modifier.clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}