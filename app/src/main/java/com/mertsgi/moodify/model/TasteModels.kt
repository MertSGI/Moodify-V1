package com.mertsgi.moodify.model

/**
 * Validated Phase 2 Taste Graph Relation Taxonomy
 */
enum class TasteRelation {
    LOVES,
    LIKES,
    AVOIDS,
    CURIOUS_ABOUT,
    WANTS_TO_TRY,
    HAS_TRIED,
    SAVED,
    NOT_INTERESTED
}

data class TasteNode(
    val id: String,
    val name: String,
    val domain: String,
    val relation: TasteRelation = TasteRelation.LOVES,
    val weight: Float = 0.85f,
    val notes: String = ""
)

data class TasteEdge(
    val fromNodeId: String,
    val toNodeId: String,
    val relationType: String,
    val strength: Float
)
