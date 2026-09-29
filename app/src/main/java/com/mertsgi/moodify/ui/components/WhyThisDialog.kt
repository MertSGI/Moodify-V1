package com.mertsgi.moodify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mertsgi.moodify.model.RecommendationItem
import com.mertsgi.moodify.ui.theme.*

@Composable
fun WhyThisDialog(
    item: RecommendationItem,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Stone700, RoundedCornerShape(24.dp))
                .testTag("why_this_dialog"),
            color = Stone900
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Transparency",
                            tint = Amber500,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Why this intervention?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_why_this_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Stone400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Item Title Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Stone850),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Amber400
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone300
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary
                Text(
                    text = "REASONING SUMMARY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Stone400
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.whyThis.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Stone100,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )

                // Matched Memories
                if (item.whyThis.matchedMemories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "MATCHED MEMORY VAULT RECORDS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Stone400
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    item.whyThis.matchedMemories.forEach { mem ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Stone800)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(AmberDim)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = mem.key,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Amber400,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = "(${mem.domain})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Stone400
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mem.snippet,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Stone300
                                )
                            }
                        }
                    }
                }

                // Context Alignment
                if (item.whyThis.contextAlignment.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CONTEXT DIMENSION FIT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Stone400
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    item.whyThis.contextAlignment.forEach { align ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald500,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Column {
                                Text(
                                    text = align.dimension,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Stone100
                                )
                                Text(
                                    text = align.reason,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Stone400
                                )
                            }
                        }
                    }
                }

                // Constraints Respected
                if (item.whyThis.constraintsRespected.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "EXPLICIT CONSTRAINTS HONORED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Stone400
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    item.whyThis.constraintsRespected.forEach { constraint ->
                        Text(
                            text = "• $constraint",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone300,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                // Novelty Score
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Familiarity vs. Novelty",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone400
                    )
                    Text(
                        text = "${(item.whyThis.noveltyScore * 100).toInt()}% Novelty",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Amber400
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { item.whyThis.noveltyScore },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Amber500,
                    trackColor = Stone800
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("understood_why_this_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                ) {
                    Text(
                        text = "Understood",
                        color = Stone950,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
