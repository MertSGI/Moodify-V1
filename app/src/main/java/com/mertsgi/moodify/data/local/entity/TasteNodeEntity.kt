package com.mertsgi.moodify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mertsgi.moodify.model.TasteNode
import com.mertsgi.moodify.model.TasteRelation

@Entity(tableName = "taste_nodes")
data class TasteNodeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val domain: String,
    val relation: String,
    val weight: Float,
    val notes: String
) {
    fun toDomain(): TasteNode = TasteNode(
        id = id,
        name = name,
        domain = domain,
        relation = try { TasteRelation.valueOf(relation) } catch (e: Exception) { TasteRelation.LOVES },
        weight = weight,
        notes = notes
    )

    companion object {
        fun fromDomain(node: TasteNode): TasteNodeEntity = TasteNodeEntity(
            id = node.id,
            name = node.name,
            domain = node.domain,
            relation = node.relation.name,
            weight = node.weight,
            notes = node.notes
        )
    }
}
