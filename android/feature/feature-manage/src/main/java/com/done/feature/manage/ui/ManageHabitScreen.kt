package com.done.feature.manage.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.done.core.ui.component.HabitCard
import com.done.core.ui.tokens.Spacing

private val EMOJIS = listOf(
    "💪", "🏃", "📖", "🧘", "💧", "🍎", "😴", "✍️",
    "🎯", "🎨", "🎵", "💻", "🌱", "🧹", "💊", "🚶",
    "🏋️", "🚴", "🧠", "📝", "☕", "🥗", "🙏", "📚"
)

private val COLORS = listOf(
    "#1D9E75", "#378ADD", "#E74C3C", "#F39C12",
    "#9B59B6", "#1ABC9C", "#E67E22", "#2ECC71",
    "#3498DB", "#E91E63", "#FF5722", "#607D8B"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManageHabitScreen(
    onBack: () -> Unit,
    viewModel: ManageHabitViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ManageUiEvent.SavedSuccessfully -> onBack()
                is ManageUiEvent.ShowError -> { /* handled via state.nameError */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isEditing) "Editar hábito" else "Novo hábito")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))

            // Name field
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onAction(ManageUiAction.UpdateName(it)) },
                label = { Text("Nome do hábito") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { error ->
                    { Text(error, color = MaterialTheme.colorScheme.error) }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Emoji picker
            Text(
                text = "Ícone",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                EMOJIS.forEach { emoji ->
                    val isSelected = state.icon == emoji
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.onAction(ManageUiAction.SelectIcon(emoji))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Color picker
            Text(
                text = "Cor",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                COLORS.forEach { colorHex ->
                    val color = try {
                        Color(colorHex.toColorInt())
                    } catch (_: Exception) {
                        Color.Gray
                    }
                    val isSelected = state.color == colorHex

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onBackground
                                else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.onAction(ManageUiAction.SelectColor(colorHex))
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Checks per day stepper
            Text(
                text = "Checks por dia",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        viewModel.onAction(
                            ManageUiAction.UpdateChecksPerDay(state.checksPerDay - 1)
                        )
                    },
                    enabled = state.checksPerDay > 1
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Diminuir")
                }
                Spacer(modifier = Modifier.width(Spacing.lg))
                Text(
                    text = "${state.checksPerDay}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(Spacing.lg))
                IconButton(
                    onClick = {
                        viewModel.onAction(
                            ManageUiAction.UpdateChecksPerDay(state.checksPerDay + 1)
                        )
                    },
                    enabled = state.checksPerDay < 20
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Live preview
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.sm))

            val previewColor = try {
                Color(state.color.toColorInt())
            } catch (_: Exception) {
                MaterialTheme.colorScheme.primary
            }

            HabitCard(
                name = state.name.ifBlank { "Nome do hábito" },
                icon = state.icon,
                habitColor = previewColor,
                checksPerDay = state.checksPerDay,
                checksToday = (state.checksPerDay / 2).coerceAtLeast(1),
                currentStreak = 7,
                bestStreak = 14,
                isCompleted = false,
                onClick = {},
                onLongClick = {}
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            Button(
                onClick = { viewModel.onAction(ManageUiAction.Save) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isSaving,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (state.isEditing) "Salvar alterações" else "Criar hábito",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}
