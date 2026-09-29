package com.mertsgi.moodify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mertsgi.moodify.model.ChatMessage
import com.mertsgi.moodify.model.MessageSender
import com.mertsgi.moodify.model.RecommendationFeedbackType
import com.mertsgi.moodify.ui.theme.*
import com.mertsgi.moodify.viewmodel.MoodifyUiState
import com.mertsgi.moodify.viewmodel.MoodifyViewModel

@Composable
fun ChatScreen(
    state: MoodifyUiState,
    viewModel: MoodifyViewModel,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(state.chatMessages.size, state.isThinking) {
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Stone950)
    ) {
        // Chat messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(state.chatMessages, key = { it.id }) { message ->
                ChatMessageItem(
                    message = message,
                    viewModel = viewModel
                )
            }

            if (state.isThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Amber500,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Moodify is synthesizing context & firewall checks...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Amber400
                        )
                    }
                }
            }
        }

        // Suggested Replies Row
        val latestAssistant = state.chatMessages.lastOrNull { it.sender == MessageSender.ASSISTANT }
        val suggestions = latestAssistant?.suggestedReplies ?: emptyList()
        if (suggestions.isNotEmpty() && !state.isThinking) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { reply ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Stone900)
                            .border(1.dp, Stone800, RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.sendMessage(reply)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("suggested_reply_chip")
                    ) {
                        Text(
                            text = reply,
                            style = MaterialTheme.typography.bodySmall,
                            color = Amber400
                        )
                    }
                }
            }
        }

        // Input Row
        Surface(
            color = Stone900,
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (state.privacySettings.isPrivateSession) "Private mode active (no durable memory)..." else "Tell Moodify how you feel or ask for plans...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone500
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Stone100,
                        unfocusedTextColor = Stone100,
                        focusedBorderColor = Amber500,
                        unfocusedBorderColor = Stone800,
                        focusedContainerColor = Stone950,
                        unfocusedContainerColor = Stone950
                    ),
                    maxLines = 3
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            viewModel.sendMessage(textToSend)
                        }
                    },
                    enabled = inputText.isNotBlank() && !state.isThinking,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !state.isThinking) Amber500 else Stone800)
                        .testTag("send_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !state.isThinking) Stone950 else Stone500
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    viewModel: MoodifyViewModel
) {
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Bubble
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(if (isUser) Amber500 else Stone900)
                .border(
                    1.dp,
                    if (isUser) Amber600 else Stone800,
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Stone950 else Stone100,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }

        // Firewall Task Badge
        message.firewallTaskId?.let { taskId ->
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Emerald500,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Firewall Filtered ($taskId)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone400
                )
            }
        }

        // Discovered Candidate Memories
        message.extractedCandidateMemories?.forEach { candidate ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Stone850),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Amber600)),
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .testTag("candidate_memory_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Amber400,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Discovered Memory Candidate",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Amber400
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${candidate.key}: \"${candidate.value}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone100
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.commitCandidateMemory(candidate) },
                            colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                            modifier = Modifier.testTag("save_candidate_memory_button")
                        ) {
                            Text("Confirm & Save to Vault", style = MaterialTheme.typography.labelSmall, color = Stone950)
                        }
                    }
                }
            }
        }

        // Embedded Cards (Recommendations or Action Proposals)
        message.cards?.forEach { card ->
            Spacer(modifier = Modifier.height(8.dp))
            if (card.type == "RECOMMENDATION") {
                card.recommendations.forEach { rec ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Stone900),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
                        modifier = Modifier
                            .widthIn(max = 340.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            rec.badge?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Amber400,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = rec.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Stone100
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = rec.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone400
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.inspectWhyThis(rec) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber400)
                                ) {
                                    Text("Why this?", style = MaterialTheme.typography.labelSmall)
                                }
                                IconButton(
                                    onClick = { viewModel.applyRecommendationFeedback(rec, RecommendationFeedbackType.SAVE_FOR_LATER) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = "Save",
                                        tint = Amber400
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (card.type == "ACTION_PROPOSAL" && card.actionPlan != null) {
                val action = card.actionPlan
                Card(
                    colors = CardDefaults.cardColors(containerColor = Stone900),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Amber600)),
                    modifier = Modifier
                        .widthIn(max = 340.dp)
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Amber500,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Action Proposal: ${action.targetProvider} (Local Mock)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Amber400
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = action.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = action.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone300
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.setPendingActionConfirmation(action) },
                            colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Review & Authorize Mock", color = Stone950, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
