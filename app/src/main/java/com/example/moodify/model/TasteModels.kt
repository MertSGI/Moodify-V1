package com.example.moodify.model

enum class TasteRelation {
    LOVES,
    LIKES,
    EXPLORING,
    AVOIDS
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
