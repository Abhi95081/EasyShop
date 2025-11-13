package com.example.easyshop.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import kotlinx.coroutines.isActive

@Composable
fun BannerView(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(180.dp) // Perfect banner height
) {
    val bannerList = listOf(
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202924/image1_xoxgla.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202932/image2_izcg8o.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202935/image3_t3lzut.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202947/image4_iu31o1.jpg"
    )

    val pagerState = rememberPagerState(initialPage = 0) { bannerList.size }

    var userScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(userScrolling) {
        if (!userScrolling) {
            while (isActive) {
                delay(3500)
                val next = (pagerState.currentPage + 1) % bannerList.size
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Box(modifier = modifier.padding(horizontal = 16.dp)) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            userScrolling = true
                            tryAwaitRelease()
                            userScrolling = false
                        }
                    )
                },
            pageSpacing = 16.dp
        ) { page ->

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(bannerList[page])
                    .crossfade(true)
                    .scale(Scale.FILL)
                    .build(),
                contentDescription = "Banner $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // DOTS INDICATOR
        CustomDotsIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            dotCount = bannerList.size,
            currentPage = pagerState.currentPage,
            currentPageOffset = { pagerState.currentPageOffsetFraction },
            activeColor = Color.White,
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
        )
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

            val offset = currentPageOffset()
            val distance = kotlin.math.abs(i - currentPage - offset)
            val activeFraction = (1f - distance).coerceIn(0f, 1f)

            val animatedSize by animateFloatAsState(
                targetValue = inactiveSize.value + (activeSize.value - inactiveSize.value) * activeFraction
            )

            val animatedAlpha by animateFloatAsState(
                targetValue = 0.4f + 0.6f * activeFraction
            )

            Box(
                modifier = Modifier
                    .size(with(LocalDensity.current) { animatedSize.dp })
                    .clip(CircleShape)
                    .alpha(animatedAlpha)
                    .background(if (activeFraction > 0.4f) activeColor else inactiveColor)
            )
        }
    }
}
