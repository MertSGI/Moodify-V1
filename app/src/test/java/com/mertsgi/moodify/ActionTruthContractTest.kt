package com.mertsgi.moodify

import com.mertsgi.moodify.model.ActionParameter
import com.mertsgi.moodify.model.ActionPlan
import com.mertsgi.moodify.model.ActionStatus
import com.mertsgi.moodify.service.ActionService
import org.junit.Assert.*
import org.junit.Test

class ActionTruthContractTest {

    @Test
    fun realExternalIntegrationCount_mustBeZero() {
        assertEquals(0, ActionService.REAL_EXTERNAL_INTEGRATION_COUNT)
    }

    @Test
    fun calendarAction_returnsMockExecution_andUnsyncedPlan() {
        val calendarAction = ActionPlan(
            id = "act_test_01",
            title = "Hold Ticket Drop",
            description = "Hold 15 min presale slot",
            targetProvider = "CALENDAR",
            actionName = "CREATE_CALENDAR_EVENT",
            parameters = listOf(
                ActionParameter("summary", "Title", "Japanese Breakfast Presale", "string"),
                ActionParameter("start", "Time", "2026-10-01T09:55:00", "date")
            ),
            status = ActionStatus.AWAITING_CONFIRMATION
        )

        val result = ActionService.executeAction(calendarAction)

        // 1. Must return MOCK_EXECUTION
        assertEquals(ActionStatus.MOCK_EXECUTION, result.executionStatus)

        // 2. Result text must explicitly state local completion and no external event
        assertTrue(result.resultSummary.contains("Mock calendar action completed locally"))
        assertTrue(result.resultSummary.contains("No external Google Calendar event was created"))

        // 3. Must NEVER claim "synced" or "external write completed"
        assertFalse(result.resultSummary.contains("synced", ignoreCase = true))
        assertFalse(result.resultSummary.contains("external write completed", ignoreCase = true))

        // 4. PlanItem must have isCalendarSynced == false
        assertNotNull(result.newPlanItem)
        assertEquals(false, result.newPlanItem?.isCalendarSynced)
    }

    @Test
    fun groceryAction_returnsMockExecution_withClearSimulationText() {
        val groceryAction = ActionPlan(
            id = "act_test_02",
            title = "Order Snacks",
            description = "High protein haul",
            targetProvider = "GROCERY",
            actionName = "CREATE_CART"
        )

        val result = ActionService.executeAction(groceryAction)
        assertEquals(ActionStatus.MOCK_EXECUTION, result.executionStatus)
        assertTrue(result.resultSummary.contains("simulated locally"))
        assertEquals(false, result.newPlanItem?.isCalendarSynced)
    }
}
