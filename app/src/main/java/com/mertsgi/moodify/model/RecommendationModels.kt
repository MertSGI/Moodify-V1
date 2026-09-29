package com.mertsgi.moodify.model

enum class RecommendationFeedbackType {
    LOVE,
    PASS,
    SAVE_FOR_LATER
}

data class MatchedMemoryRef(
    val key: String,
    val snippet: String,
    val domain: String
)

data class ContextAlignmentRef(
    val dimension: String,
    val reason: String
)

data class TasteFactorRef(
    val tasteNodeName: String,
    val relation: String,
    val weightReason: String
)

data class WhyThisExplanation(
    val summary: String,
    val matchedMemories: List<MatchedMemoryRef> = emptyList(),
    val contextAlignment: List<ContextAlignmentRef> = emptyList(),
    val tasteFactor: List<TasteFactorRef> = emptyList(),
    val constraintsRespected: List<String> = emptyList(),
    val noveltyScore: Float = 0.2f
)

data class RecommendationMetadata(
    val duration: String? = null,
    val genreOrCuisine: String? = null,
    val rating: String? = null,
    val effortLevel: String? = null,
    val price: String? = null,
    val location: String? = null
)

data class RecommendationItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val domain: String,
    val description: String,
    val badge: String? = null,
    val imageUrl: String? = null,
    val metadata: RecommendationMetadata = RecommendationMetadata(),
    val whyThis: WhyThisExplanation,
    val actionPrompt: String = "Save to Plan",
    val actionType: String = "SAVE_PLAN",
    val score: Float = 0.95f,
    val feedbackGiven: RecommendationFeedbackType? = null
)
