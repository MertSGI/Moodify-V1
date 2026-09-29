package com.mertsgi.moodify.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mertsgi.moodify.model.*
import com.mertsgi.moodify.ui.theme.*
import com.mertsgi.moodify.viewmodel.MoodifyUiState
import com.mertsgi.moodify.viewmodel.MoodifyViewModel

enum class YouSubTab {
    MEMORY_VAULT,
    TASTE_GRAPH,
    FIREWALL,
    INTEGRATIONS,
    SETTINGS
}

@Composable
fun YouScreen(
    state: MoodifyUiState,
    viewModel: MoodifyViewModel,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableStateOf(YouSubTab.MEMORY_VAULT) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Stone950)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Stone900),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
                modifier = Modifier.fillMaxWidth().testTag("user_profile_header")
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AmberDim)
                            .border(1.5.dp, Amber500, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AC",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Amber400
                        )
                    }

                    Column {
                        Text(
                            text = state.user.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                        Text(
                            text = "${state.user.role} · ${state.user.timezone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone400
                        )
                    }
                }
            }
        }

        // Sub-Navigation Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                YouSubTab.entries.forEach { tab ->
                    val isSelected = subTab == tab
                    val label = when (tab) {
                        YouSubTab.MEMORY_VAULT -> "Memory Vault (${state.memories.size})"
                        YouSubTab.TASTE_GRAPH -> "Taste Graph (${state.tasteNodes.size})"
                        YouSubTab.FIREWALL -> "Privacy Firewall"
                        YouSubTab.INTEGRATIONS -> "Integrations"
                        YouSubTab.SETTINGS -> "Settings"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { subTab = tab },
                        label = { Text(label) },
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
                        modifier = Modifier.testTag("subtab_${tab.name.lowercase()}")
                    )
                }
            }
        }

        // 1. MEMORY VAULT VIEW
        if (subTab == YouSubTab.MEMORY_VAULT) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXPLICIT PERSISTENT MEMORIES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Amber400
                    )
                    Button(
                        onClick = { viewModel.setIsAddingNewMemory(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                        modifier = Modifier.testTag("add_memory_vault_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Stone950)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Memory", color = Stone950, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(state.memories, key = { it.id }) { memory ->
                MemoryItemCard(
                    memory = memory,
                    onEdit = { viewModel.setEditingMemory(memory) },
                    onDelete = { viewModel.deleteMemory(memory.id) }
                )
            }
        }

        // 2. TASTE GRAPH VIEW
        if (subTab == YouSubTab.TASTE_GRAPH) {
            item {
                Text(
                    text = "CALIBRATED TASTE ENTITIES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Amber400
                )
            }

            items(state.tasteNodes, key = { it.id }) { node ->
                TasteNodeCard(node = node)
            }
        }

        // 3. PRIVACY & CONTEXT FIREWALL VIEW
        if (subTab == YouSubTab.FIREWALL) {
            item {
                FirewallSettingsCard(
                    state = state,
                    viewModel = viewModel
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "RECENT CONTEXT FIREWALL DECISIONS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Amber400
                )
            }

            if (state.firewallLogs.isEmpty()) {
                item {
                    Text(
                        text = "No decisions logged yet. Start a chat or scenario to see live filtration.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone500
                    )
                }
            } else {
                items(state.firewallLogs, key = { it.id }) { decision ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Stone900),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = decision.taskId,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald500
                                )
                                Text(
                                    text = decision.timestamp.take(19).replace("T", " "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Stone500
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Task: \"${decision.userRequestSnippet}\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Stone100
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = decision.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone300
                            )
                            if (decision.admittedMemoryKeys.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Admitted: ${decision.admittedMemoryKeys.joinToString(", ")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Amber400
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. INTEGRATIONS VIEW
        if (subTab == YouSubTab.INTEGRATIONS) {
            item {
                Text(
                    text = "INTEGRATION STATUS TRUTH CONTRACT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Amber400
                )
            }

            items(state.integrations, key = { it.id }) { provider ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Stone900),
                    shape = RoundedCornerShape(18.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = provider.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Stone100
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (provider.status) {
                                                IntegrationStatus.MOCK -> AmberDim
                                                IntegrationStatus.NOT_CONNECTED -> Stone800
                                                IntegrationStatus.LIVE -> Emerald500.copy(alpha = 0.2f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = provider.status.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (provider.status) {
                                            IntegrationStatus.MOCK -> Amber400
                                            IntegrationStatus.NOT_CONNECTED -> Stone400
                                            IntegrationStatus.LIVE -> Emerald500
                                        }
                                    )
                                }
                            }
                            Text(
                                text = provider.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone400
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = provider.syncDisplayText,
                                style = MaterialTheme.typography.labelSmall,
                                color = Amber400
                            )
                        }

                        Switch(
                            checked = provider.status == IntegrationStatus.MOCK || provider.status == IntegrationStatus.LIVE,
                            onCheckedChange = { viewModel.toggleIntegration(provider.id) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Amber500,
                                checkedTrackColor = AmberDim
                            )
                        )
                    }
                }
            }
        }

        // 5. SETTINGS VIEW
        if (subTab == YouSubTab.SETTINGS) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Stone900),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Proactivity Policy (Phase 3 Engine)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Mode: ${state.proactiveSettings.mode.name} (Safety Cap: ${state.proactiveSettings.mode.safetyCap} pings/day)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone300
                        )
                        Text(
                            text = "Effective Daily Cap: ${state.proactiveSettings.effectiveDailyMax} · Local Calendar Date Authority",
                            style = MaterialTheme.typography.bodySmall,
                            color = Amber400
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "SELECT PROACTIVE MODE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Stone400
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProactiveMode.entries.forEach { mode ->
                                val isSelected = state.proactiveSettings.mode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.updateProactiveSettings(
                                            state.proactiveSettings.copy(mode = mode)
                                        )
                                    },
                                    label = { Text("${mode.name} (${mode.safetyCap})") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Amber500,
                                        selectedLabelColor = Stone950
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Stone800)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Data Portability & Database Reset",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Resets Room database and DataStore preferences back to initial fixtures.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone400
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.resetToSeedData() },
                            colors = ButtonDefaults.buttonColors(containerColor = Rose500),
                            modifier = Modifier.testTag("reset_seed_data_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Stone100)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Everything to Seed Data", color = Stone100, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryItemCard(
    memory: MemoryItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Stone900),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
        modifier = Modifier.fillMaxWidth().testTag("memory_item_${memory.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AmberDim)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = memory.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Amber400
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (memory.sensitivity) {
                                    SensitivityLevel.NORMAL -> Stone800
                                    SensitivityLevel.PERSONAL -> AmberDim
                                    SensitivityLevel.SENSITIVE -> Amber600.copy(alpha = 0.25f)
                                    SensitivityLevel.HIGHLY_SENSITIVE -> Rose500.copy(alpha = 0.25f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = memory.sensitivity.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (memory.sensitivity) {
                                SensitivityLevel.NORMAL -> Stone300
                                SensitivityLevel.PERSONAL -> Amber400
                                SensitivityLevel.SENSITIVE -> Amber500
                                SensitivityLevel.HIGHLY_SENSITIVE -> Rose500
                            }
                        )
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Stone400, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Rose500, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = memory.key,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Stone100
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = memory.value,
                style = MaterialTheme.typography.bodyMedium,
                color = Stone300
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Source: ${memory.source}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone500
                )
                Text(
                    text = if (memory.allowedForExternalTools) "External Tools Allowed" else "Internal Only",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (memory.allowedForExternalTools) Emerald500 else Amber400
                )
            }
        }
    }
}

@Composable
fun TasteNodeCard(node: TasteNode) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Stone900),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Stone100
                )
                Text(
                    text = "Domain: ${node.domain} · Weight: ${(node.weight * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone400
                )
                if (node.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = node.notes,
                        style = MaterialTheme.typography.labelSmall,
                        color = Stone500
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (node.relation) {
                            TasteRelation.LOVES -> AmberDim
                            TasteRelation.LIKES, TasteRelation.HAS_TRIED -> Emerald500.copy(alpha = 0.2f)
                            TasteRelation.EXPLORING, TasteRelation.CURIOUS_ABOUT, TasteRelation.WANTS_TO_TRY, TasteRelation.SAVED -> Sky500.copy(alpha = 0.2f)
                            TasteRelation.AVOIDS, TasteRelation.NOT_INTERESTED -> Rose500.copy(alpha = 0.2f)
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = node.relation.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when (node.relation) {
                        TasteRelation.LOVES -> Amber400
                        TasteRelation.LIKES, TasteRelation.HAS_TRIED -> Emerald500
                        TasteRelation.EXPLORING, TasteRelation.CURIOUS_ABOUT, TasteRelation.WANTS_TO_TRY, TasteRelation.SAVED -> Sky500
                        TasteRelation.AVOIDS, TasteRelation.NOT_INTERESTED -> Rose500
                    }
                )
            }
        }
    }
}

@Composable
fun FirewallSettingsCard(
    state: MoodifyUiState,
    viewModel: MoodifyViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Stone900),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
        modifier = Modifier.fillMaxWidth().testTag("firewall_settings_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Personal Context Firewall Controls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Stone100
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Zero-trust privacy boundary enforcing Minimal-Purpose Context Assembly.",
                style = MaterialTheme.typography.bodySmall,
                color = Stone400
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Private Session Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Private Session (Incognito)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Stone100
                    )
                    Text(
                        text = "Zero durable memories created. Private context discarded on exit.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone400
                    )
                }

                Switch(
                    checked = state.privacySettings.isPrivateSession,
                    onCheckedChange = {
                        viewModel.updatePrivacySettings(
                            state.privacySettings.copy(isPrivateSession = it)
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Amber500,
                        checkedTrackColor = AmberDim
                    ),
                    modifier = Modifier.testTag("private_session_switch")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Max Sensitivity Threshold (4 levels)
            Text(
                text = "MAXIMUM SENSITIVITY ALLOWED FOR INFERENCE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Stone400
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SensitivityLevel.entries.forEach { level ->
                    val isSelected = state.privacySettings.maxAllowedSensitivity == level
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.updatePrivacySettings(
                                state.privacySettings.copy(maxAllowedSensitivity = level)
                            )
                        },
                        label = { Text(level.name, fontSize = MaterialTheme.typography.labelSmall.fontSize) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Amber500,
                            selectedLabelColor = Stone950
                        )
                    )
                }
            }
        }
    }
}
