package com.done.core.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.done.core.ui.theme.TealDark
import com.done.core.ui.theme.TealLight
import com.done.core.ui.theme.TealPrimary
import com.done.core.ui.tokens.Spacing
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class ContributionDay(
    val date: LocalDate,
    val checkCount: Int,
    val checksPerDay: Int
) {
    val level: ContributionLevel
        get() = when {
            checkCount == 0 -> ContributionLevel.NONE
            checkCount < checksPerDay -> ContributionLevel.PARTIAL
            else -> ContributionLevel.COMPLETE
        }
}

enum class ContributionLevel { NONE, PARTIAL, COMPLETE }

@Composable
fun ContributionGrid(
    days: List<ContributionDay>,
    modifier: Modifier = Modifier,
    weeks: Int = 13
) {
    val noneColor = MaterialTheme.colorScheme.surfaceVariant
    val partialColor = TealLight.copy(alpha = 0.5f)
    val completeColor = TealPrimary

    Column(modifier = modifier) {
        val monthLabels = days
            .groupBy { it.date.month }
            .mapValues { (_, v) -> v.first().date }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .size(width = (weeks * 14 + 20).dp, height = (7 * 14).dp)
        ) {
            val cellSize = 10.dp.toPx()
            val gap = 3.dp.toPx()
            val totalDays = weeks * 7
            val startIndex = (totalDays - days.size).coerceAtLeast(0)

            days.forEachIndexed { index, day ->
                val globalIndex = startIndex + index
                val col = globalIndex / 7
                val row = globalIndex % 7
                val x = col * (cellSize + gap)
                val y = row * (cellSize + gap)

                val color = when (day.level) {
                    ContributionLevel.NONE -> noneColor
                    ContributionLevel.PARTIAL -> partialColor
                    ContributionLevel.COMPLETE -> completeColor
                }

                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Menos",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(noneColor, partialColor, completeColor).forEach { color ->
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawRoundRect(
                            color = color,
                            cornerRadius = CornerRadius(2.dp.toPx())
                        )
                    }
                }
            }

            Text(
                text = "Mais",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
