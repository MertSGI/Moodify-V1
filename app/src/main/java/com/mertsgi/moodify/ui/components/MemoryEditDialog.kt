package com.mertsgi.moodify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mertsgi.moodify.model.MemoryItem
import com.mertsgi.moodify.model.SensitivityLevel
import com.mertsgi.moodify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryEditDialog(
    initialMemory: MemoryItem?,
    onSave: (MemoryItem) -> Unit,
    onDismiss: () -> Unit
) {
    var keyText by remember { mutableStateOf(initialMemory?.key ?: "") }
    var valueText by remember { mutableStateOf(initialMemory?.value ?: "") }
    var categoryText by remember { mutableStateOf(initialMemory?.category ?: "preferences") }
    var sensitivity by remember { mutableStateOf(initialMemory?.sensitivity ?: SensitivityLevel.NORMAL) }
    var allowExternalTools by remember { mutableStateOf(initialMemory?.allowedForExternalTools ?: true) }
    var isImportant by remember { mutableStateOf(initialMemory?.isImportant ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Stone700, RoundedCornerShape(24.dp))
                .testTag("memory_edit_dialog"),
            color = Stone900
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialMemory == null) "Add Memory Vault Entry" else "Edit Memory Record",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Stone100
                )
                Text(
                    text = "Durable facts shaping recommendations & context",
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone400
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = keyText,
                    onValueChange = { keyText = it },
                    label = { Text("Memory Key (e.g. food_texture)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_key_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Stone100,
                        unfocusedTextColor = Stone100,
                        focusedBorderColor = Amber500,
                        unfocusedBorderColor = Stone700
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = valueText,
                    onValueChange = { valueText = it },
                    label = { Text("Belief / Value") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("memory_value_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Stone100,
                        unfocusedTextColor = Stone100,
                        focusedBorderColor = Amber500,
                        unfocusedBorderColor = Stone700
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = categoryText,
                    onValueChange = { categoryText = it },
                    label = { Text("Category (identity, food, music, work...)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Stone100,
                        unfocusedTextColor = Stone100,
                        focusedBorderColor = Amber500,
                        unfocusedBorderColor = Stone700
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SENSITIVITY CLASSIFICATION",
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
                        val selected = sensitivity == level
                        FilterChip(
                            selected = selected,
                            onClick = { sensitivity = level },
                            label = { Text(level.name, fontSize = MaterialTheme.typography.labelSmall.fontSize) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Amber500,
                                selectedLabelColor = Stone950
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Allow for External Tools",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone300
                    )
                    Switch(
                        checked = allowExternalTools,
                        onCheckedChange = { allowExternalTools = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Amber500,
                            checkedTrackColor = AmberDim
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mark as High Importance",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone300
                    )
                    Switch(
                        checked = isImportant,
                        onCheckedChange = { isImportant = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Amber500,
                            checkedTrackColor = AmberDim
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Stone300)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (keyText.isNotBlank() && valueText.isNotBlank()) {
                                onSave(
                                    MemoryItem(
                                        id = initialMemory?.id ?: "mem_${System.currentTimeMillis()}",
                                        key = keyText.trim(),
                                        value = valueText.trim(),
                                        category = categoryText.trim(),
                                        sensitivity = sensitivity,
                                        allowedForExternalTools = allowExternalTools,
                                        isImportant = isImportant,
                                        userConfirmed = true
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_memory_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                    ) {
                        Text(
                            text = "Save Record",
                            color = Stone950,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
