package com.mertsgi.moodify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.mertsgi.moodify.model.ActionPlan
import com.mertsgi.moodify.model.ActionStatus
import com.mertsgi.moodify.model.PlanItem
import com.mertsgi.moodify.ui.theme.*
import com.mertsgi.moodify.viewmodel.MoodifyUiState
import com.mertsgi.moodify.viewmodel.MoodifyViewModel

@Composable
fun PlansScreen(
    state: MoodifyUiState,
    viewModel: MoodifyViewModel,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Plans & Actions",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Stone100
                    )
                    Text(
                        text = "Commitments, watchlists & local mock actions",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone400
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                    modifier = Modifier.testTag("add_plan_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Stone950)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", color = Stone950, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Action Plans Section (Local Mock Dispatch)
        if (state.actionPlans.isNotEmpty()) {
            item {
                Text(
                    text = "ACTION DISPATCH (LOCAL SIMULATION)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Amber400
                )
            }

            items(state.actionPlans, key = { it.id }) { action ->
                ActionPlanCard(action = action, viewModel = viewModel)
            }
        }

        // Plan Items Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SAVED PLANS & LISTS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Stone400
            )
        }

        items(state.plans, key = { it.id }) { plan ->
            PlanItemCard(plan = plan, onToggleItem = { planId, itemId ->
                viewModel.togglePlanItemChecked(planId, itemId)
            })
        }
    }

    if (showAddDialog) {
        AddPlanDialog(
            onSave = { title, type, category, notes ->
                viewModel.addPlanItem(title, type, category, notes)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun ActionPlanCard(
    action: ActionPlan,
    viewModel: MoodifyViewModel
) {
    val isPending = action.status == ActionStatus.AWAITING_CONFIRMATION
    val isExecuted = action.status == ActionStatus.MOCK_EXECUTION || action.status == ActionStatus.SUCCEEDED

    Card(
        colors = CardDefaults.cardColors(containerColor = Stone900),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isPending) Amber600 else Stone800)
        ),
        modifier = Modifier.fillMaxWidth().testTag("action_plan_card_${action.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isExecuted) Icons.Default.CheckCircle else Icons.Default.LockClock,
                        contentDescription = null,
                        tint = if (isExecuted) Emerald500 else Amber500,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Target: ${action.targetProvider} (Mock)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Stone300
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (action.status) {
                                ActionStatus.PROPOSED,
                                ActionStatus.AWAITING_CONFIRMATION -> AmberDim
                                ActionStatus.CONFIRMED,
                                ActionStatus.MOCK_EXECUTION,
                                ActionStatus.SUCCEEDED -> Emerald500.copy(alpha = 0.2f)
                                ActionStatus.FAILED,
                                ActionStatus.CANCELLED -> Rose500.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = action.status.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (action.status) {
                            ActionStatus.PROPOSED,
                            ActionStatus.AWAITING_CONFIRMATION -> Amber400
                            ActionStatus.CONFIRMED,
                            ActionStatus.MOCK_EXECUTION,
                            ActionStatus.SUCCEEDED -> Emerald500
                            ActionStatus.FAILED,
                            ActionStatus.CANCELLED -> Rose500
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Stone100
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = action.description,
                style = MaterialTheme.typography.bodySmall,
                color = Stone300
            )

            if (action.resultSummary != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Stone850)
                        .padding(8.dp)
                ) {
                    Text(
                        text = action.resultSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald500
                    )
                }
            }

            if (isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.setPendingActionConfirmation(action) },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                    ) {
                        Text("Review & Authorize Mock", color = Stone950, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.rejectAction(action.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Stone300)
                    ) {
                        Text("Decline")
                    }
                }
            }
        }
    }
}

@Composable
fun PlanItemCard(
    plan: PlanItem,
    onToggleItem: (String, String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Stone900),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Stone800)),
        modifier = Modifier.fillMaxWidth().testTag("plan_item_card_${plan.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Stone800)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = plan.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Amber400,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (plan.type == "CALENDAR_HOLD") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Stone850)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Sync: False (Mock)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Stone400
                            )
                        }
                    }
                }

                plan.date?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Stone400
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Stone100
            )

            if (plan.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plan.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = Stone300
                )
            }

            // Subitems / Checklist
            if (plan.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    plan.items.forEach { subItem ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleItem(plan.id, subItem.id) }
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = subItem.checked,
                                onCheckedChange = { onToggleItem(plan.id, subItem.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Amber500,
                                    uncheckedColor = Stone500,
                                    checkmarkColor = Stone950
                                )
                            )
                            Text(
                                text = subItem.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (subItem.checked) Stone500 else Stone100,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddPlanDialog(
    onSave: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Personal Unwind") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add to Plans", color = Stone100, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Plan Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, "ACTIVITY", category, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Amber500)
            ) {
                Text("Save", color = Stone950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Stone400)
            }
        },
        containerColor = Stone900
    )
}
