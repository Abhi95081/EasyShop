package com.example.easyshop.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.easyshop.R

@Composable
fun BannerView(modifier: Modifier = Modifier) {

    val bannerList = listOf(
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202924/image1_xoxgla.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202932/image2_izcg8o.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202935/image3_t3lzut.jpg",
        "https://res.cloudinary.com/dm711rjty/image/upload/v1761202947/image4_iu31o1.jpg"
    )

    val pageState = rememberPagerState(initialPage = 0) {
        bannerList.size
    }

    Column(modifier = modifier) {

        HorizontalPager(
            state = pageState,
            pageSpacing = 16.dp,
            modifier = Modifier.height(200.dp) // fixed height
        ) { page ->

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(bannerList[page])
                    .crossfade(true)
                    .scale(Scale.FILL)
                    .build(),
                contentDescription = "Banner Image $page", // Updated content description for accessibility
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dots Indicator
        DotsIndicator(
            dotCount = bannerList.size,
            type = ShiftIndicatorType(
                DotGraphic(
                    color = MaterialTheme.colorScheme.primary,
                    size = 6.dp
                )
            ),
            pagerState = pageState
        )
    }

    // ✅ Auto-scroll every 3 seconds
    LaunchedEffect(pageState) {
        while (true) {
            delay(3000)
            val nextPage = (pageState.currentPage + 1) % bannerList.size
            pageState.animateScrollToPage(nextPage)
        }
    }
}