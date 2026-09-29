package com.example.moodify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.moodify.model.ActionPlan
import com.example.moodify.ui.theme.*

@Composable
fun ActionConfirmDialog(
    action: ActionPlan,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    Dialog(onDismissRequest = onReject) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Amber600, RoundedCornerShape(24.dp))
                .testTag("action_confirm_dialog"),
            color = Stone900
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AmberDim),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Confirmation Required",
                            tint = Amber500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Explicit Authorization Needed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Stone100
                        )
                        Text(
                            text = "Target: ${action.targetProvider} · Consequential Action",
                            style = MaterialTheme.typography.bodySmall,
                            color = Amber400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Stone100
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Stone300
                )

                if (action.parameters.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "PROPOSED PARAMETERS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Stone400
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    action.parameters.forEach { param ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = param.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone400
                            )
                            Text(
                                text = param.value,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Stone100
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Stone800)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "🔒 Safety Sandbox: Moodify never writes to external APIs without your explicit 1-tap confirmation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone300
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reject_action_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Stone300),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Stone700))
                    ) {
                        Text("Decline")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_action_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                    ) {
                        Text(
                            text = "Authorize",
                            color = Stone950,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
