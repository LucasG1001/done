package com.done.core.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.done.core.ui.theme.SegmentEmpty
import com.done.core.ui.theme.SegmentEmptyDark
import com.done.core.ui.tokens.ProgressTokens

@Composable
fun SegmentedProgressBar(
    totalSegments: Int,
    filledSegments: Int,
    filledColor: Color,
    modifier: Modifier = Modifier,
    emptyColor: Color = if (isSystemInDarkTheme()) SegmentEmptyDark else SegmentEmpty
) {
    val animatedFilled by animateFloatAsState(
        targetValue = filledSegments.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "filled_segments"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(ProgressTokens.SegmentHeight)
    ) {
        val gap = ProgressTokens.SegmentGap.toPx()
        val radius = ProgressTokens.SegmentRadius.toPx()
        val totalGaps = (totalSegments - 1) * gap
        val segmentWidth = (size.width - totalGaps) / totalSegments

        for (i in 0 until totalSegments) {
            val x = i * (segmentWidth + gap)
            val isFilled = i < animatedFilled
            drawRoundRect(
                color = if (isFilled) filledColor else emptyColor,
                topLeft = Offset(x, 0f),
                size = Size(segmentWidth, size.height),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}
