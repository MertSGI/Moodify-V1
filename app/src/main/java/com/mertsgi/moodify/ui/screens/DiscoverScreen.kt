package com.mertsgi.moodify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.mertsgi.moodify.model.RecommendationFeedbackType
import com.mertsgi.moodify.model.RecommendationItem
import com.mertsgi.moodify.ui.theme.*
import com.mertsgi.moodify.viewmodel.MoodifyUiState
import com.mertsgi.moodify.viewmodel.MoodifyViewModel

val CATEGORIES = listOf(
    "ALL" to "All Domains",
    "music" to "Music & Sound",
    "movies_tv" to "Cinema & Series",
    "events" to "Live Shows",
    "products" to "Workday Fuel",
    "food" to "Dining & Comfort"
)

@Composable
fun DiscoverScreen(
    state: MoodifyUiState,
    viewModel: MoodifyViewModel,
    modifier: Modifier = Modifier
) {
    val filteredRecs = if (state.activeCategoryFilter == "ALL") {
        state.recommendations
    } else {
        state.recommendations.filter { it.category == state.activeCategoryFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Stone950)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Taste & Discovery",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Stone100
                )
                Text(
                    text = "Calibrated interventions balanced between comfort anchors and fresh novelty",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone400
                )
            }
        }

        // Exploration Factor Slider
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Stone900),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exploration_slider_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Exploration Factor",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                        Text(
                            text = "${(state.explorationFactor * 100).toInt()}% Novelty",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Amber400
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Slider(
                        value = state.explorationFactor,
                        onValueChange = { viewModel.setExplorationFactor(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exploration_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = Amber500,
                            activeTrackColor = Amber500,
                            inactiveTrackColor = Stone800
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Comfort & Familiar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Stone400
                        )
                        Text(
                            text = "Fresh & Serendipitous",
                            style = MaterialTheme.typography.labelSmall,
                            color = Stone400
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CATEGORIES.forEach { (catKey, catLabel) ->
                    val isSelected = state.activeCategoryFilter == catKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategoryFilter(catKey) },
                        label = { Text(catLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Amber500,
                            selectedLabelColor = Stone950,
                            containerColor = Stone900,
                            labelColor = Stone300
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Amber500 else Stone800
                        ),
                        modifier = Modifier.testTag("filter_chip_$catKey")
                    )
                }
            }
        }

        // Recommendation Items List
        items(filteredRecs, key = { it.id }) { item ->
            DiscoverCardItem(
                item = item,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun DiscoverCardItem(
    item: RecommendationItem,
    viewModel: MoodifyViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Stone900),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("discover_rec_item_${item.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.badge?.let {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AmberDim)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Amber400
                        )
                    }
                }
                Text(
                    text = "${(item.score * 100).toInt()}% Match",
                    style = MaterialTheme.typography.labelSmall,
                    color = Emerald500,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Stone100
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Stone400
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Stone300
            )

            // Metadata Chips
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.metadata.duration?.let {
                    MetadataPill(text = it)
                }
                item.metadata.genreOrCuisine?.let {
                    MetadataPill(text = it)
                }
                item.metadata.price?.let {
                    MetadataPill(text = it)
                }
                item.metadata.effortLevel?.let {
                    MetadataPill(text = "Effort: $it")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.inspectWhyThis(item) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber400),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Amber600)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Why this?", style = MaterialTheme.typography.labelSmall)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { viewModel.applyRecommendationFeedback(item, RecommendationFeedbackType.PASS) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Stone800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = "Pass",
                            tint = if (item.feedbackGiven == RecommendationFeedbackType.PASS) Rose500 else Stone400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.applyRecommendationFeedback(item, RecommendationFeedbackType.SAVE_FOR_LATER) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Stone800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (item.feedbackGiven == RecommendationFeedbackType.SAVE_FOR_LATER) Amber400 else Stone400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.applyRecommendationFeedback(item, RecommendationFeedbackType.LOVE) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Stone800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Love",
                            tint = if (item.feedbackGiven == RecommendationFeedbackType.LOVE) Rose500 else Stone400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetadataPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Stone800)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Stone300
        )
    }
}
