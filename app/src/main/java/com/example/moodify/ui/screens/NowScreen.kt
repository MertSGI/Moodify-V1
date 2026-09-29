package com.example.moodify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moodify.model.ActionStatus
import com.example.moodify.model.ContextualDimensions
import com.example.moodify.model.RecommendationFeedbackType
import com.example.moodify.ui.theme.*
import com.example.moodify.viewmodel.AppTab
import com.example.moodify.viewmodel.MoodifyUiState
import com.example.moodify.viewmodel.MoodifyViewModel

@Composable
fun NowScreen(
    state: MoodifyUiState,
    viewModel: MoodifyViewModel,
    modifier: Modifier = Modifier
) {
    val topRec = state.recommendations.firstOrNull()
    val pendingAction = state.actionPlans.find { it.status == ActionStatus.AWAITING_CONFIRMATION }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Stone950)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // 1. Alive Greeting & Atmospheric Presence
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sparkles,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${state.context.timeOfDay} · ${state.context.dayOfWeek}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Amber400
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Good evening, ${state.user.preferredName}.",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Stone100
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = state.context.secondaryState,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Stone400
                )
            }
        }

        // 2. Contextual State Card & Dimension Vectors
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Stone900),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("context_assessment_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AmberDim),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryChargingFull,
                                    contentDescription = null,
                                    tint = Amber500,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "CONTEXT ASSESSMENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Stone400
                                )
                                Text(
                                    text = state.context.primaryState.replace("_", " "),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Stone100
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Stone800)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "~${state.context.freeHoursRemainingToday}h free",
                                style = MaterialTheme.typography.labelSmall,
                                color = Stone300
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dimension Vector Bars
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DimensionBar(
                            name = "Energy",
                            value = state.context.dimensions.energy,
                            tint = Amber500,
                            onIncrement = {
                                viewModel.updateContextDimensions(
                                    state.context.dimensions.copy(
                                        energy = (state.context.dimensions.energy + 0.1f).coerceAtMost(1.0f)
                                    )
                                )
                            },
                            onDecrement = {
                                viewModel.updateContextDimensions(
                                    state.context.dimensions.copy(
                                        energy = (state.context.dimensions.energy - 0.1f).coerceAtLeast(0.05f)
                                    )
                                )
                            }
                        )
                        DimensionBar(
                            name = "Stress / Cognitive Load",
                            value = state.context.dimensions.stress,
                            tint = Rose500,
                            onIncrement = {
                                viewModel.updateContextDimensions(
                                    state.context.dimensions.copy(
                                        stress = (state.context.dimensions.stress + 0.1f).coerceAtMost(1.0f)
                                    )
                                )
                            },
                            onDecrement = {
                                viewModel.updateContextDimensions(
                                    state.context.dimensions.copy(
                                        stress = (state.context.dimensions.stress - 0.1f).coerceAtLeast(0.05f)
                                    )
                                )
                            }
                        )
                        DimensionBar(
                            name = "Social Drive",
                            value = state.context.dimensions.socialNeed,
                            tint = Sky500,
                            onIncrement = {
                                viewModel.updateContextDimensions(
                                    state.context.dimensions.copy(
                                        socialNeed = (state.context.dimensions.socialNeed + 0.1f).coerceAtMost(1.0f)
                                    )
                                )
                            },
                            onDecrement = {
                                viewModel.updateContextDimensions(
                                    state.context.dimensions.copy(
                                        socialNeed = (state.context.dimensions.socialNeed - 0.1f).coerceAtLeast(0.05f)
                                    )
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset Quick Check-in Chips
                    Text(
                        text = "QUICK STATE SWITCHER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Stone400
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetChip(label = "Low Battery", isSelected = state.context.primaryState == "LOW_BATTERY") {
                            viewModel.updateContextDimensions(
                                ContextualDimensions(energy = 0.2f, stress = 0.75f, focusNeed = 0.1f),
                                "LOW_BATTERY"
                            )
                        }
                        PresetChip(label = "Deep Focus", isSelected = state.context.primaryState == "DEEP_FOCUS") {
                            viewModel.updateContextDimensions(
                                ContextualDimensions(energy = 0.7f, stress = 0.3f, focusNeed = 0.85f),
                                "DEEP_FOCUS"
                            )
                        }
                        PresetChip(label = "Social Weekend", isSelected = state.context.primaryState == "WEEKEND_EXPLORATION") {
                            viewModel.updateContextDimensions(
                                ContextualDimensions(energy = 0.8f, stress = 0.2f, socialNeed = 0.85f),
                                "WEEKEND_EXPLORATION"
                            )
                        }
                    }
                }
            }
        }

        // 3. Pending Action Authorization Card (if available)
        if (pendingAction != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Stone900),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Amber600)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pending_action_banner")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Amber500,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Action Awaiting Authorization",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Amber400
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pendingAction.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Stone100
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = pendingAction.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone300
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setPendingActionConfirmation(pendingAction) },
                                colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                                modifier = Modifier.testTag("review_action_button")
                            ) {
                                Text("Review & Authorize", color = Stone950, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.rejectAction(pendingAction.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Stone300)
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }
        }

        // 4. Primary Recommended Intervention
        if (topRec != null) {
            item {
                Text(
                    text = "PRIMARY INTERVENTION FOR TONIGHT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Amber400
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Stone900),
                    shape = RoundedCornerShape(24.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("top_rec_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            topRec.badge?.let { badge ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AmberDim)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Amber400
                                    )
                                }
                            }
                            Text(
                                text = "${(topRec.score * 100).toInt()}% Match",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald500,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = topRec.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = topRec.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Amber400
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = topRec.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Stone300
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.inspectWhyThis(topRec) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber400),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Amber600)
                                ),
                                modifier = Modifier.testTag("why_this_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Why this?")
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = {
                                        viewModel.applyRecommendationFeedback(
                                            topRec,
                                            RecommendationFeedbackType.SAVE_FOR_LATER
                                        )
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Stone800)
                                        .testTag("save_rec_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = "Save for later",
                                        tint = if (topRec.feedbackGiven == RecommendationFeedbackType.SAVE_FOR_LATER) Amber400 else Stone300
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.applyRecommendationFeedback(
                                            topRec,
                                            RecommendationFeedbackType.LOVE
                                        )
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Stone800)
                                        .testTag("love_rec_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Love this",
                                        tint = if (topRec.feedbackGiven == RecommendationFeedbackType.LOVE) Rose500 else Stone300
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Jump to Chat prompt
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Stone850),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectTab(AppTab.CHAT) }
                    .testTag("chat_jump_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Amber500
                        )
                        Column {
                            Text(
                                text = "Talk to Moodify",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Stone100
                            )
                            Text(
                                text = "Explore evening plans or tell me about your day",
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone400
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open chat",
                        tint = Amber400
                    )
                }
            }
        }
    }
}

@Composable
fun DimensionBar(
    name: String,
    value: Float,
    tint: androidx.compose.ui.graphics.Color,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = Stone300
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${(value * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = tint
                )
                Text(
                    text = "-",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Stone800)
                        .clickable { onDecrement() }
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone300
                )
                Text(
                    text = "+",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Stone800)
                        .clickable { onIncrement() }
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone300
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = tint,
            trackColor = Stone800
        )
    }
}

@Composable
fun PresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Amber500 else Stone800)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Stone950 else Stone300
        )
    }
}
