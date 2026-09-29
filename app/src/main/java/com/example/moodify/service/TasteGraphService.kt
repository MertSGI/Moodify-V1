package com.example.moodify.service

import com.example.moodify.model.RecommendationFeedbackType
import com.example.moodify.model.TasteNode
import com.example.moodify.model.TasteRelation

object TasteGraphService {
    fun applyFeedback(
        nodes: List<TasteNode>,
        itemTitle: String,
        domain: String,
        feedback: RecommendationFeedbackType
    ): List<TasteNode> {
        val existingIndex = nodes.indexOfFirst { it.name.equals(itemTitle, ignoreCase = true) }
        val updated = nodes.toMutableList()

        when (feedback) {
            RecommendationFeedbackType.LOVE -> {
                if (existingIndex >= 0) {
                    val current = updated[existingIndex]
                    updated[existingIndex] = current.copy(
                        relation = TasteRelation.LOVES,
                        weight = (current.weight + 0.1f).coerceAtMost(1.0f)
                    )
                } else {
                    updated.add(
                        TasteNode(
                            id = "tn_${System.currentTimeMillis()}",
                            name = itemTitle,
                            domain = domain,
                            relation = TasteRelation.LOVES,
                            weight = 0.85f,
                            notes = "Affinity reinforced through explicit user approval."
                        )
                    )
                }
            }
            RecommendationFeedbackType.PASS -> {
                if (existingIndex >= 0) {
                    val current = updated[existingIndex]
                    updated[existingIndex] = current.copy(
                        relation = TasteRelation.AVOIDS,
                        weight = (current.weight - 0.2f).coerceAtLeast(0.1f)
                    )
                } else {
                    updated.add(
                        TasteNode(
                            id = "tn_${System.currentTimeMillis()}",
                            name = itemTitle,
                            domain = domain,
                            relation = TasteRelation.AVOIDS,
                            weight = 0.6f,
                            notes = "User passed on this item."
                        )
                    )
                }
            }
            RecommendationFeedbackType.SAVE_FOR_LATER -> {
                if (existingIndex >= 0) {
                    val current = updated[existingIndex]
                    updated[existingIndex] = current.copy(
                        relation = TasteRelation.LIKES,
                        weight = (current.weight + 0.05f).coerceAtMost(0.95f)
                    )
                } else {
                    updated.add(
                        TasteNode(
                            id = "tn_${System.currentTimeMillis()}",
                            name = itemTitle,
                            domain = domain,
                            relation = TasteRelation.EXPLORING,
                            weight = 0.75f,
                            notes = "Saved to user plans for future follow-up."
                        )
                    )
                }
            }
        }
        return updated
    }
}
