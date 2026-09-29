package com.mertsgi.moodify.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mertsgi.moodify.ui.components.ActionConfirmDialog
import com.mertsgi.moodify.ui.components.MemoryEditDialog
import com.mertsgi.moodify.ui.components.ScenarioBar
import com.mertsgi.moodify.ui.components.WhyThisDialog
import com.mertsgi.moodify.ui.screens.*
import com.mertsgi.moodify.ui.theme.*
import com.mertsgi.moodify.viewmodel.AppTab
import com.mertsgi.moodify.viewmodel.MoodifyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodifyApp(
    viewModel: MoodifyViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigation 3 developer-owned state-driven back stack
    val backStack = remember { mutableStateListOf(AppTab.NOW) }

    // Synchronize if currentTab changes programmatically (e.g. from scenarios or recommendations)
    LaunchedEffect(state.currentTab) {
        if (backStack.lastOrNull() != state.currentTab) {
            backStack.add(state.currentTab)
        }
    }

    fun navigateTo(tab: AppTab) {
        if (state.currentTab != tab) {
            backStack.add(tab)
            viewModel.selectTab(tab)
        }
    }

    // Predictive back / BackHandler: Navigation 3 developer-managed back stack pop
    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
        val prevTab = backStack.lastOrNull() ?: AppTab.NOW
        viewModel.selectTab(prevTab)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Stone950)
    ) {
        // Material 3 Adaptive Threshold: >= 600dp usable width uses NavigationRail
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Adaptive Side Navigation Rail Layout (Foldables / Landscape / Tablets)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                NavigationRail(
                    containerColor = Stone900,
                    modifier = Modifier
                        .fillMaxHeight()
                        .border(width = 1.dp, color = Stone800)
                        .testTag("adaptive_navigation_rail")
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Moodify",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Amber500,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    NavigationRailItem(
                        selected = state.currentTab == AppTab.NOW,
                        onClick = { navigateTo(AppTab.NOW) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Now") },
                        label = { Text("Now") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Stone950,
                            selectedTextColor = Amber400,
                            indicatorColor = Amber500,
                            unselectedIconColor = Stone400,
                            unselectedTextColor = Stone400
                        ),
                        modifier = Modifier.testTag("nav_rail_now")
                    )
                    NavigationRailItem(
                        selected = state.currentTab == AppTab.CHAT,
                        onClick = { navigateTo(AppTab.CHAT) },
                        icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chat") },
                        label = { Text("Chat") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Stone950,
                            selectedTextColor = Amber400,
                            indicatorColor = Amber500,
                            unselectedIconColor = Stone400,
                            unselectedTextColor = Stone400
                        ),
                        modifier = Modifier.testTag("nav_rail_chat")
                    )
                    NavigationRailItem(
                        selected = state.currentTab == AppTab.DISCOVER,
                        onClick = { navigateTo(AppTab.DISCOVER) },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Discover") },
                        label = { Text("Discover") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Stone950,
                            selectedTextColor = Amber400,
                            indicatorColor = Amber500,
                            unselectedIconColor = Stone400,
                            unselectedTextColor = Stone400
                        ),
                        modifier = Modifier.testTag("nav_rail_discover")
                    )
                    NavigationRailItem(
                        selected = state.currentTab == AppTab.PLANS,
                        onClick = { navigateTo(AppTab.PLANS) },
                        icon = { Icon(Icons.Default.Checklist, contentDescription = "Plans") },
                        label = { Text("Plans") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Stone950,
                            selectedTextColor = Amber400,
                            indicatorColor = Amber500,
                            unselectedIconColor = Stone400,
                            unselectedTextColor = Stone400
                        ),
                        modifier = Modifier.testTag("nav_rail_plans")
                    )
                    NavigationRailItem(
                        selected = state.currentTab == AppTab.YOU,
                        onClick = { navigateTo(AppTab.YOU) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "You") },
                        label = { Text("You") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Stone950,
                            selectedTextColor = Amber400,
                            indicatorColor = Amber500,
                            unselectedIconColor = Stone400,
                            unselectedTextColor = Stone400
                        ),
                        modifier = Modifier.testTag("nav_rail_you")
                    )
                }

                // Main Content area
                Column(modifier = Modifier.fillMaxSize()) {
                    TopBrandAndScenarioHeader(state = state, viewModel = viewModel, onTriggerScenario = {
                        navigateTo(AppTab.CHAT)
                        viewModel.triggerScenario(it)
                    })
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (state.currentTab) {
                            AppTab.NOW -> NowScreen(state = state, viewModel = viewModel)
                            AppTab.CHAT -> ChatScreen(state = state, viewModel = viewModel)
                            AppTab.DISCOVER -> DiscoverScreen(state = state, viewModel = viewModel)
                            AppTab.PLANS -> PlansScreen(state = state, viewModel = viewModel)
                            AppTab.YOU -> YouScreen(state = state, viewModel = viewModel)
                        }
                    }
                }
            }
        } else {
            // Standard Compact Portrait Layout (Phone)
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Stone950)
                    .statusBarsPadding(),
                topBar = {
                    TopBrandAndScenarioHeader(state = state, viewModel = viewModel, onTriggerScenario = {
                        navigateTo(AppTab.CHAT)
                        viewModel.triggerScenario(it)
                    })
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
                            onClick = { navigateTo(AppTab.NOW) },
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
                            onClick = { navigateTo(AppTab.CHAT) },
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
                            onClick = { navigateTo(AppTab.DISCOVER) },
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
                            onClick = { navigateTo(AppTab.PLANS) },
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
                            onClick = { navigateTo(AppTab.YOU) },
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

@Composable
private fun TopBrandAndScenarioHeader(
    state: MoodifyUiState,
    viewModel: MoodifyViewModel,
    onTriggerScenario: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().background(Stone950)) {
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
                            text = "Private Session (Incognito)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Rose500,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "Android Phone (Native)",
                style = MaterialTheme.typography.labelSmall,
                color = Stone500
            )
        }

        ScenarioBar(
            onSelectScenario = onTriggerScenario
        )
    }
}
