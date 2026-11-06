package com.dhirajmane.splitcrew.ui.groups

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dhirajmane.splitcrew.R
import com.dhirajmane.splitcrew.ui.theme.BrandNavy
import com.dhirajmane.splitcrew.ui.theme.BrandTeal
import kotlinx.coroutines.delay
import java.util.Calendar

private val BrandGreenSplash = Color(0xFF2BB673)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(24f) }
    val lineWidth = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val greetingAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, tween(600))
        logoScale.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        titleAlpha.animateTo(1f, tween(500))
        titleOffset.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
        lineWidth.animateTo(1f, tween(400))
        taglineAlpha.animateTo(1f, tween(400))
        greetingAlpha.animateTo(1f, tween(400))
        delay(1200)
        onFinished()
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Hello, night owl"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BrandNavy, Color(0xFF0E5A6E), BrandTeal))),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .alpha(logoAlpha.value)
                .background(
                    Brush.radialGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(168.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(36.dp))
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = null,
                    modifier = Modifier.size(136.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .padding(top = titleOffset.value.dp)
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width((120 * lineWidth.value).dp)
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color.Transparent, BrandGreenSplash, Color.Transparent)),
                        RoundedCornerShape(2.dp)
                    )
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(id = R.string.tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value)
            )

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .alpha(greetingAlpha.value)
                    .background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(50))
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "$greeting, let's split the bill!",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}