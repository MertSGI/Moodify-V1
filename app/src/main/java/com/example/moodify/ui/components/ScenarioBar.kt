package com.example.moodify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moodify.ui.theme.*

data class ScenarioInfo(
    val id: String,
    val label: String,
    val subtitle: String
)

val SCENARIOS = listOf(
    ScenarioInfo("A", "A: Rough Day", "Low Battery / Offline reset"),
    ScenarioInfo("B", "B: Friday Out", "Thalia Hall / Calendar Action"),
    ScenarioInfo("C", "C: Workday Snacks", "<$25 Savory protein list"),
    ScenarioInfo("D", "D: Review Recap", "Marcus VP / Rest celebration"),
    ScenarioInfo("E", "E: Movie Night", "Severance / Past Lives"),
    ScenarioInfo("F", "F: Concert Radar", "Japanese Breakfast Tour")
)

@Composable
fun ScenarioBar(
    onSelectScenario: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Stone950)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Amber500,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "INTERACTIVE SCENARIOS (1-CLICK TEST BENCH)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Amber400
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SCENARIOS.forEach { scenario ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Stone900)
                        .border(1.dp, Stone800, RoundedCornerShape(12.dp))
                        .clickable { onSelectScenario(scenario.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("scenario_btn_${scenario.id}")
                ) {
                    Column {
                        Text(
                            text = scenario.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                        Text(
                            text = scenario.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone400
                        )
                    }
                }
            }
        }
    }
}
