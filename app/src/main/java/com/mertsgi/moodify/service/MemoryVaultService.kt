package com.mertsgi.moodify.service

import com.mertsgi.moodify.model.CandidateMemory
import com.mertsgi.moodify.model.SensitivityLevel

object MemoryVaultService {
    fun extractCandidateMemories(text: String): List<CandidateMemory> {
        val lower = text.lowercase()
        val candidates = mutableListOf<CandidateMemory>()

        if (lower.contains("hate") || lower.contains("dislike") || lower.contains("no sugary")) {
            candidates.add(
                CandidateMemory(
                    id = "cand_${System.currentTimeMillis()}_1",
                    category = "dislikes",
                    key = "food_texture_preference",
                    value = "Dislikes heavy processed sugars during work; strongly prefers savory",
                    sourceQuote = text,
                    confidence = 0.94f,
                    sensitivity = SensitivityLevel.NORMAL,
                    explanation = "Direct sentiment expressed regarding snacks or food choices."
                )
            )
        }

        if (lower.contains("japanese breakfast") || lower.contains("thalia hall")) {
            candidates.add(
                CandidateMemory(
                    id = "cand_${System.currentTimeMillis()}_2",
                    category = "music",
                    key = "tracked_artist",
                    value = "Actively interested in live concert tracking for Japanese Breakfast at Thalia Hall",
                    sourceQuote = text,
                    confidence = 0.98f,
                    sensitivity = SensitivityLevel.NORMAL,
                    explanation = "Explicit artist watch request detected in user query."
                )
            )
        }

        if (lower.contains("under $") || lower.contains("budget")) {
            candidates.add(
                CandidateMemory(
                    id = "cand_${System.currentTimeMillis()}_3",
                    category = "budget_preferences",
                    key = "spending_target",
                    value = "Stated personal spend constraint for casual daily expenses",
                    sourceQuote = text,
                    confidence = 0.91f,
                    sensitivity = SensitivityLevel.NORMAL,
                    explanation = "Budget boundary mentioned during product exploration."
                )
            )
        }

        return candidates
    }
}
