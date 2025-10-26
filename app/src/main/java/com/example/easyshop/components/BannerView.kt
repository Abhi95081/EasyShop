package com.example.easyshop.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import kotlinx.coroutines.delay

@Composable
fun BannerView(modifier: Modifier = Modifier) {

    val bannerList = listOf(
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202924/image1_xoxgla.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202932/image2_izcg8o.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202935/image3_t3lzut.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202947/image4_iu31o1.jpg"
    )

    val pageState = rememberPagerState(initialPage = 0) { bannerList.size }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pageState,
            pageSpacing = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
        ) { page ->

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(bannerList[page])
                    .crossfade(true)
                    .scale(Scale.FILL)
                    .build(),
                contentDescription = "Banner Image $page",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Overlay dots at the bottom center of the banner
        CustomDotsIndicator(
            modifier = Modifier
                .align(BottomCenter)
                .padding(bottom = 8.dp),
            dotCount = bannerList.size,
            currentPage = pageState.currentPage,
            currentPageOffset = { pageState.currentPageOffsetFraction },
            activeColor = Color.White,
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            activeSize = 9.dp,
            inactiveSize = 6.dp,
            spacing = 8.dp,
            onDotClicked = { targetPage ->
                // if you want clicking dots to navigate:
                // launch a coroutine to animate scroll; but we can't call animateScrollToPage here
                // because this lambda is called from UI - you can implement navigation if needed.
            }
        )
    }

    // Auto-scroll every 3 seconds
    LaunchedEffect(pageState) {
        while (true) {
            delay(1000)
            val nextPage = (pageState.currentPage + 1) % bannerList.size
            pageState.animateScrollToPage(nextPage)
        }
    }
}

@Composable
fun CustomDotsIndicator(
    modifier: Modifier = Modifier,
    dotCount: Int,
    currentPage: Int,
    currentPageOffset: () -> Float = { 0f },
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.Gray,
    activeSize: Dp = 10.dp,
    inactiveSize: Dp = 6.dp,
    spacing: Dp = 8.dp,
    onDotClicked: ((Int) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until dotCount) {
            // compute animation factor for this dot: 1.0 when fully active, 0.0 otherwise
            val offset = currentPageOffset()
            val distanceFromActive = kotlin.math.abs(i - currentPage - offset)
            // distanceFromActive is 0 when i is the active page, approaches 1 for neighbors, etc.
            val activeFraction = (1f - distanceFromActive).coerceIn(0f, 1f)

            // animate the size smoothly
            val targetSize = inactiveSize.value + (activeSize.value - inactiveSize.value) * activeFraction
            val animatedSize by animateFloatAsState(targetSize)

            // animate alpha for better contrast
            val targetAlpha = 0.5f + 0.5f * activeFraction
            val animatedAlpha by animateFloatAsState(targetAlpha)

            Box(
                modifier = Modifier
                    .size(with(LocalDensity.current) { animatedSize.dp })
                    .clip(CircleShape)
                    .alpha(animatedAlpha)
                    .background(if (activeFraction > 0.4f) activeColor else inactiveColor)
                    .pointerInput(i) {
                        if (onDotClicked != null) {
                            detectTapGestures {
                                onDotClicked(i)
                            }
                        }
                    }
            )
        }
    }
}
