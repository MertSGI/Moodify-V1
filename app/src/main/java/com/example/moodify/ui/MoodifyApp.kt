package com.example.moodify.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodify.ui.components.ActionConfirmDialog
import com.example.moodify.ui.components.MemoryEditDialog
import com.example.moodify.ui.components.ScenarioBar
import com.example.moodify.ui.components.WhyThisDialog
import com.example.moodify.ui.screens.*
import com.example.moodify.ui.theme.*
import com.example.moodify.viewmodel.AppTab
import com.example.moodify.viewmodel.MoodifyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodifyApp(
    viewModel: MoodifyViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Back handling: if not in NOW tab, return to NOW
    BackHandler(enabled = state.currentTab != AppTab.NOW) {
        viewModel.selectTab(AppTab.NOW)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Stone950)
            .statusBarsPadding(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(Stone950)) {
                // Top Brand Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Moodify",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Amber500
                        )
                        if (state.privacySettings.isPrivateSession) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .background(Rose500.copy(alpha = 0.2f), shape = MaterialTheme.shapes.extraSmall)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Private Session",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Rose500,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "v1.0 (Android)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Stone500
                    )
                }

                // Interactive 1-Click Scenario Bar
                ScenarioBar(
                    onSelectScenario = { scenarioId ->
                        viewModel.triggerScenario(scenarioId)
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Stone900,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(width = 1.dp, color = Stone800)
                    .testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = state.currentTab == AppTab.NOW,
                    onClick = { viewModel.selectTab(AppTab.NOW) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Now") },
                    label = { Text("Now") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Stone950,
                        selectedTextColor = Amber400,
                        indicatorColor = Amber500,
                        unselectedIconColor = Stone400,
                        unselectedTextColor = Stone400
                    ),
                    modifier = Modifier.testTag("nav_now")
                )
                NavigationBarItem(
                    selected = state.currentTab == AppTab.CHAT,
                    onClick = { viewModel.selectTab(AppTab.CHAT) },
                    icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chat") },
                    label = { Text("Chat") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Stone950,
                        selectedTextColor = Amber400,
                        indicatorColor = Amber500,
                        unselectedIconColor = Stone400,
                        unselectedTextColor = Stone400
                    ),
                    modifier = Modifier.testTag("nav_chat")
                )
                NavigationBarItem(
                    selected = state.currentTab == AppTab.DISCOVER,
                    onClick = { viewModel.selectTab(AppTab.DISCOVER) },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Discover") },
                    label = { Text("Discover") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Stone950,
                        selectedTextColor = Amber400,
                        indicatorColor = Amber500,
                        unselectedIconColor = Stone400,
                        unselectedTextColor = Stone400
                    ),
                    modifier = Modifier.testTag("nav_discover")
                )
                NavigationBarItem(
                    selected = state.currentTab == AppTab.PLANS,
                    onClick = { viewModel.selectTab(AppTab.PLANS) },
                    icon = { Icon(Icons.Default.Checklist, contentDescription = "Plans") },
                    label = { Text("Plans") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Stone950,
                        selectedTextColor = Amber400,
                        indicatorColor = Amber500,
                        unselectedIconColor = Stone400,
                        unselectedTextColor = Stone400
                    ),
                    modifier = Modifier.testTag("nav_plans")
                )
                NavigationBarItem(
                    selected = state.currentTab == AppTab.YOU,
                    onClick = { viewModel.selectTab(AppTab.YOU) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "You") },
                    label = { Text("You") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Stone950,
                        selectedTextColor = Amber400,
                        indicatorColor = Amber500,
                        unselectedIconColor = Stone400,
                        unselectedTextColor = Stone400
                    ),
                    modifier = Modifier.testTag("nav_you")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state.currentTab) {
                AppTab.NOW -> NowScreen(state = state, viewModel = viewModel)
                AppTab.CHAT -> ChatScreen(state = state, viewModel = viewModel)
                AppTab.DISCOVER -> DiscoverScreen(state = state, viewModel = viewModel)
                AppTab.PLANS -> PlansScreen(state = state, viewModel = viewModel)
                AppTab.YOU -> YouScreen(state = state, viewModel = viewModel)
            }
        }
    }

    // Global "Why This?" Transparency Modal
    state.inspectingWhyThisItem?.let { item ->
        WhyThisDialog(
            item = item,
            onDismiss = { viewModel.inspectWhyThis(null) }
        )
    }

    // Global Action Confirmation Modal for consequential writes
    state.pendingActionConfirmation?.let { action ->
        ActionConfirmDialog(
            action = action,
            onConfirm = { viewModel.confirmAndExecuteAction(action.id) },
            onReject = { viewModel.rejectAction(action.id) }
        )
    }

    // Global Memory Add / Edit Dialog
    if (state.editingMemory != null || state.isAddingNewMemory) {
        MemoryEditDialog(
            initialMemory = state.editingMemory,
            onSave = { updatedMemory ->
                if (state.editingMemory != null) {
                    viewModel.updateMemory(updatedMemory.id, updatedMemory)
                } else {
                    viewModel.addMemory(updatedMemory)
                }
                viewModel.setEditingMemory(null)
                viewModel.setIsAddingNewMemory(false)
            },
            onDismiss = {
                viewModel.setEditingMemory(null)
                viewModel.setIsAddingNewMemory(false)
            }
        )
    }
}
