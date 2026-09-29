package com.example.moodify.model

enum class MessageSender {
    USER,
    ASSISTANT
}

data class ChatCardPayload(
    val type: String, // RECOMMENDATION, ACTION_PROPOSAL
    val title: String,
    val recommendations: List<RecommendationItem> = emptyList(),
    val actionPlan: ActionPlan? = null
)

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: String,
    val suggestedReplies: List<String> = emptyList(),
    val cards: List<ChatCardPayload>? = null,
    val extractedCandidateMemories: List<CandidateMemory>? = null,
    val firewallTaskId: String? = null
)
