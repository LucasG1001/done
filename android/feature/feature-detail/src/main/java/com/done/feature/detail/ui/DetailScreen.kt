package com.done.feature.detail.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.done.core.ui.component.ContributionDay
import com.done.core.ui.component.ContributionGrid
import com.done.core.ui.component.StatCard
import com.done.core.ui.theme.StreakOrange
import com.done.core.ui.theme.TrophyGold
import com.done.core.ui.tokens.Spacing
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailUiEvent.NavigateToEdit -> onNavigateToEdit(event.habitId)
                DetailUiEvent.Archived -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.habit?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val habit = state.habit ?: return@Scaffold

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.lg)
                    .verticalScroll(rememberScrollState())
            ) {
                // Habit header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = Spacing.md)
                ) {
                    Text(text = habit.icon, fontSize = 40.sp)
                    Spacer(modifier = Modifier.size(Spacing.md))
                    Column {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${habit.checksPerDay}x ao dia",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    StatCard(
                        value = "${state.currentStreak}",
                        label = "streak atual",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "${state.bestStreak}",
                        label = "melhor streak",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "${(state.completionRate30Days * 100).toInt()}%",
                        label = "últimos 30d",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                // Contribution grid
                Text(
                    text = "Histórico",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(Spacing.sm))

                val contributionDays = state.contributionDays.map { day ->
                    ContributionDay(
                        date = LocalDate.parse(day.date),
                        checkCount = day.checkCount,
                        checksPerDay = day.checksPerDay
                    )
                }
                ContributionGrid(
                    days = contributionDays,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                // Bar chart for last 14 days
                Text(
                    text = "Últimos 14 dias",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(Spacing.sm))

                val habitColor = try {
                    Color(habit.color.toColorInt())
                } catch (_: Exception) {
                    MaterialTheme.colorScheme.primary
                }

                BarChart(
                    data = state.recentDays.map { it.checkCount },
                    maxValue = habit.checksPerDay,
                    barColor = habitColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(Spacing.xxl))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Button(
                        onClick = { viewModel.onAction(DetailUiAction.Edit) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.size(Spacing.sm))
                        Text("Editar")
                    }
                    OutlinedButton(
                        onClick = { viewModel.onAction(DetailUiAction.Archive) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null)
                        Spacer(modifier = Modifier.size(Spacing.sm))
                        Text("Arquivar")
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxxl))
            }
        }
    }
}

@Composable
private fun BarChart(
    data: List<Int>,
    maxValue: Int,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val bgColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        val barCount = data.size
        val gap = 6.dp.toPx()
        val barWidth = (size.width - (barCount - 1) * gap) / barCount
        val maxHeight = size.height

        data.forEachIndexed { index, value ->
            val normalizedHeight = if (maxValue > 0) {
                (value.toFloat() / maxValue).coerceIn(0f, 1f) * maxHeight
            } else 0f

            val x = index * (barWidth + gap)

            // Background bar
            drawRoundRect(
                color = bgColor,
                topLeft = Offset(x, 0f),
                size = Size(barWidth, maxHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // Filled bar
            if (normalizedHeight > 0f) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, maxHeight - normalizedHeight),
                    size = Size(barWidth, normalizedHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }
    }
}
