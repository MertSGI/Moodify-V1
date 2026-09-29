package com.example.moodify.service

import com.example.moodify.model.*

data class AgentProcessingResult(
    val updatedContext: ContextSnapshot,
    val extractedCandidates: List<CandidateMemory>,
    val replyMessage: ChatMessage,
    val firewallDecisionId: String
)

object AgentOrchestrator {
    fun processUserMessage(
        userText: String,
        currentContext: ContextSnapshot,
        memories: List<MemoryItem>,
        tasteNodes: List<TasteNode>,
        allRecommendations: List<RecommendationItem>,
        privacySettings: PrivacySettings
    ): AgentProcessingResult {
        val textLower = userText.lowercase()

        // 1. Context interpretation
        val updatedContext = ContextEngine.inferContextFromText(currentContext, userText)

        // 2. Candidate memory extraction
        val extractedCandidates = if (privacySettings.isPrivateSession) {
            emptyList()
        } else {
            MemoryVaultService.extractCandidateMemories(userText)
        }

        // 3. Personal Context Firewall filtering
        val firewallResult = FirewallService.filterContextForTask(
            userText.take(60),
            "RECOMMENDER",
            memories,
            privacySettings
        )
        val admittedMemories = firewallResult.admitted
        val firewallTaskId = firewallResult.decision.taskId

        // 4. Response formulation & cards
        val cards = mutableListOf<ChatCardPayload>()
        val responseText: String
        val suggestedReplies: List<String>

        when {
            // SCENARIO A: ROUGH DAY / LOW BATTERY
            textLower.contains("awful") || textLower.contains("don't really want to think") || textLower.contains("dont really want to think") || textLower.contains("exhausted") -> {
                responseText = "I hear you Alex. Let's protect your evening. When your battery is drained like this, the worst thing is having to make decisions.\n\nI've picked three zero-friction ways to let your nervous system reset tonight—one quiet sound, one gentle watch, and one tiny offline reset. Just pick whatever requires the least effort."

                val musicRec = allRecommendations.find { it.id == "rec_01" } ?: allRecommendations[0]
                val movieRec = allRecommendations.find { it.id == "rec_02" } ?: allRecommendations[1]

                val activityRec = RecommendationItem(
                    id = "rec_act_reset",
                    title = "Warm Shower + 15-Minute Foam Roll",
                    subtitle = "Zero screen engagement, physical reset",
                    category = "activities",
                    domain = "activities",
                    description = "No podcast, no bright screens. Dim lighting, hot water to ease shoulder tension, and gentle floor stretching.",
                    badge = "Offline Reset",
                    metadata = RecommendationMetadata(duration = "20 mins", effortLevel = "VERY_LOW"),
                    whyThis = WhyThisExplanation(
                        summary = "Tailored for high tension and low battery (stress 0.8, energy 0.15) without needing mental effort.",
                        matchedMemories = listOf(
                            MatchedMemoryRef("boundaries", "Low cognitive load during evening recovery", "boundaries")
                        ),
                        contextAlignment = listOf(
                            ContextAlignmentRef("Energy (0.15)", "Zero screens or decision-making required")
                        ),
                        constraintsRespected = listOf("No screens", "Immediate home action"),
                        noveltyScore = 0.1f
                    ),
                    actionPrompt = "Save to Evening Plan",
                    actionType = "SAVE_PLAN",
                    score = 0.99f
                )

                cards.add(
                    ChatCardPayload(
                        type = "RECOMMENDATION",
                        title = "3 Low-Friction Options for Tonight",
                        recommendations = listOf(musicRec, movieRec, activityRec)
                    )
                )

                suggestedReplies = listOf(
                    "The ambient sound sounds perfect.",
                    "I'll put on After the Storm.",
                    "Just going to do the shower reset and head to sleep early."
                )
            }

            // SCENARIO B: FRIDAY NIGHT OUT / CONCERT
            textLower.contains("friday") || textLower.contains("ready for the weekend") || textLower.contains("concert") || textLower.contains("free tonight") || textLower.contains("celebrate") -> {
                responseText = "Looking at your schedule snapshot, your Friday night is open. Because you love intimate indie rock shows and smaller venues like Thalia Hall, here is an exciting live show match:"

                val concertRec = allRecommendations.find { it.id == "rec_03" } ?: allRecommendations[2]

                val calendarProposal = ActionPlan(
                    id = "act_${System.currentTimeMillis()}",
                    title = "Schedule Ticket Drop Hold (Mock)",
                    description = "Set a 15-minute simulated hold for \"Japanese Breakfast Ticket Drop\" on Thursday at 9:55 AM (local prototype simulation).",
                    targetProvider = "CALENDAR",
                    actionName = "CREATE_CALENDAR_EVENT",
                    riskLevel = "EXTERNAL_WRITE",
                    requiresExplicitConfirmation = true,
                    parameters = listOf(
                        ActionParameter("summary", "Event Title", "Japanese Breakfast Presale Alert", "string"),
                        ActionParameter("start", "Time", "2026-10-01T09:55:00", "date"),
                        ActionParameter("duration", "Duration", "15 mins", "string")
                    ),
                    status = ActionStatus.AWAITING_CONFIRMATION
                )

                cards.add(
                    ChatCardPayload(
                        type = "RECOMMENDATION",
                        title = "Matched Live Show in Chicago",
                        recommendations = listOf(concertRec)
                    )
                )
                cards.add(
                    ChatCardPayload(
                        type = "ACTION_PROPOSAL",
                        title = "Proposed Action (Requires Confirmation)",
                        actionPlan = calendarProposal
                    )
                )

                suggestedReplies = listOf(
                    "Authorize simulated calendar hold.",
                    "Why did you recommend this specific venue?",
                    "Show me something more low-key instead."
                )
            }

            // SCENARIO C: PRODUCT DISCOVERY (Snacks under budget)
            textLower.contains("snack") || textLower.contains("junk") || textLower.contains("groceries") -> {
                responseText = "Remembering that you hate afternoon sugar crashes and prefer keeping weekday food hauls under $25, I put together this high-protein savory snack trio for your studio desk:"

                val snackRec = allRecommendations.find { it.id == "rec_04" } ?: allRecommendations[3]
                cards.add(
                    ChatCardPayload(
                        type = "RECOMMENDATION",
                        title = "Workday Savory Snack Haul",
                        recommendations = listOf(snackRec)
                    )
                )

                suggestedReplies = listOf(
                    "Add all three to my Shopping List in Plans.",
                    "Can you swap the roasted almonds for pumpkin seeds?",
                    "Show the budget breakdown."
                )
            }

            // SCENARIO C2: FOOD / INEXPENSIVE DINNER
            textLower.contains("inexpensive") || textLower.contains("dinner") || textLower.contains("something to eat") || textLower.contains("what to eat") || textLower.contains("comfort food") -> {
                val comfortFoodMem = admittedMemories.find { it.category == "food" }
                val budgetMem = admittedMemories.find { it.category == "budget_preferences" }

                val foodDetail = comfortFoodMem?.value ?: "comfort food"
                val budgetDetail = budgetMem?.value ?: "keeping it casual and inexpensive"

                responseText = "I checked your preferences through the Context Firewall (${admittedMemories.size} relevant food & budget constraints admitted, unrelated work, travel, and personal domains withheld).\n\nBased on $foodDetail and $budgetDetail, here is a warm, comforting dinner pick for tonight:"

                val foodRec = allRecommendations.find { it.id == "rec_05" } ?: allRecommendations[4]
                cards.add(
                    ChatCardPayload(
                        type = "RECOMMENDATION",
                        title = "Comfort Dinner Match (Under Budget)",
                        recommendations = listOf(foodRec)
                    )
                )

                suggestedReplies = listOf(
                    "Save this to my Evening Plan.",
                    "Why did you recommend this specific spot?",
                    "Show me the firewall context breakdown."
                )
            }

            // SCENARIO D: FOLLOW-UP ON DESIGN REVIEW
            textLower.contains("marcus") || textLower.contains("review") || textLower.contains("went well") || textLower.contains("interview") -> {
                responseText = "That's great to hear, Alex. That mobile redesign was a huge milestone and you'd put a lot of thought into the interaction patterns. Since you've been running at high intensity all week, give yourself credit for getting that through approval.\n\nWant to celebrate with a good dinner or ease gently into the weekend?"

                val ramenRec = allRecommendations.find { it.id == "rec_05" } ?: allRecommendations[4]
                cards.add(
                    ChatCardPayload(
                        type = "RECOMMENDATION",
                        title = "Solo Comfort Celebration",
                        recommendations = listOf(ramenRec)
                    )
                )

                suggestedReplies = listOf(
                    "Ramen at Menya Goku sounds amazing right now.",
                    "I just want a movie and couch tonight.",
                    "What's on my schedule tomorrow morning?"
                )
            }

            // SCENARIO E: MOVIE NIGHT (3 Differentiated Choices)
            textLower.contains("movie") || textLower.contains("watch") || textLower.contains("tired but") || textLower.contains("boring") -> {
                responseText = "When you're mentally tired but still want something sharp, you need films that are captivating without demanding spreadsheet-level focus. Here are 3 genuinely differentiated picks matching your taste:"

                val movieChoices = listOf(
                    allRecommendations.find { it.id == "rec_02" } ?: allRecommendations[1],
                    RecommendationItem(
                        id = "rec_mv_02",
                        title = "Severance (Season 1, Ep 1-3)",
                        subtitle = "Immaculate workplace mystery & dry dark satire",
                        category = "movies_tv",
                        domain = "series",
                        description = "Surgical design aesthetic, hypnotic pacing, and sharp commentary on corporate compartmentalization.",
                        badge = "High Taste Satire",
                        metadata = RecommendationMetadata(duration = "54m per episode", genreOrCuisine = "Dystopian Thriller / Satire", rating = "97% match", effortLevel = "LOW"),
                        whyThis = WhyThisExplanation(
                            summary = "Matches your design eye and affinity for smart workplace tension without loud sensory overload.",
                            matchedMemories = listOf(
                                MatchedMemoryRef("profession", "Appreciates high-production design in cinema", "identity")
                            ),
                            contextAlignment = listOf(
                                ContextAlignmentRef("Novelty (0.7)", "Clever hooks that keep you intrigued without exhausting you")
                            ),
                            tasteFactor = listOf(
                                TasteFactorRef("Severance", "LOVES", "Saved in your favorite series")
                            ),
                            constraintsRespected = listOf("Available on Apple TV+", "Episodic bite sizes"),
                            noveltyScore = 0.45f
                        ),
                        actionPrompt = "Add to Watch Later",
                        actionType = "WATCH_LATER",
                        score = 0.95f
                    ),
                    RecommendationItem(
                        id = "rec_mv_03",
                        title = "Past Lives (Celine Song)",
                        subtitle = "Poignant, beautifully shot romantic drama in NYC & Seoul",
                        category = "movies_tv",
                        domain = "movies",
                        description = "Two deeply connected childhood friends are reunited across two decades. Quiet, profound, emotionally restorative.",
                        badge = "Emotional Resonance",
                        metadata = RecommendationMetadata(duration = "1h 46m", genreOrCuisine = "Quiet Drama", rating = "98% on RT", effortLevel = "LOW"),
                        whyThis = WhyThisExplanation(
                            summary = "A thoughtful, warm cinematic experience with stunning photography that feels rich rather than disposable.",
                            matchedMemories = listOf(
                                MatchedMemoryRef("comfort_cinema", "Appreciates quiet, tender cinema", "movies_tv")
                            ),
                            contextAlignment = listOf(
                                ContextAlignmentRef("Valence (0.1)", "Emotionally grounding without heavy cynicism")
                            ),
                            constraintsRespected = listOf("Sub-2 hour runtime", "High critical consensus"),
                            noveltyScore = 0.6f
                        ),
                        actionPrompt = "Add to Watch Later",
                        actionType = "WATCH_LATER",
                        score = 0.92f
                    )
                )

                cards.add(
                    ChatCardPayload(
                        type = "RECOMMENDATION",
                        title = "3 Distinct Styles for Tonight",
                        recommendations = movieChoices
                    )
                )

                suggestedReplies = listOf(
                    "Save After the Storm to my watch list.",
                    "Severance sounds like the right balance of sharp and relaxing.",
                    "Tell me why you picked Past Lives."
                )
            }

            // SCENARIO F: CONCERT WATCH / ARTIST RADAR
            textLower.contains("japanese breakfast") || textLower.contains("track") || textLower.contains("tour") -> {
                responseText = "I have Japanese Breakfast on your live concert tracking radar. Here is the latest confirmed intimate date, plus Thalia Hall venue details:"

                val concertRec = allRecommendations.find { it.id == "rec_03" } ?: allRecommendations[2]
                cards.add(
                    ChatCardPayload(
                        type = "RECOMMENDATION",
                        title = "Concert Radar Alert",
                        recommendations = listOf(concertRec)
                    )
                )

                suggestedReplies = listOf(
                    "Set a calendar alert for the presale.",
                    "Add to my Tracked Concerts list in Plans.",
                    "Are there any other artists touring soon?"
                )
            }

            // FALLBACK GENERAL CONVERSATION
            else -> {
                responseText = "I understand, Alex. Based on what you've shared with me so far, I'm keeping your current state in mind (low battery, evening hours, quiet recharge). How can I make tonight feel easier for you?"

                suggestedReplies = listOf(
                    "Show me something easy to watch tonight.",
                    "Find me a quiet ramen spot nearby.",
                    "What do you know about my music taste?",
                    "Explain the Personal Context Firewall."
                )
            }
        }

        val reply = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = MessageSender.ASSISTANT,
            text = responseText,
            timestamp = java.time.Instant.now().toString(),
            suggestedReplies = suggestedReplies,
            cards = cards.ifEmpty { null },
            extractedCandidateMemories = extractedCandidates.ifEmpty { null },
            firewallTaskId = firewallTaskId
        )

        return AgentProcessingResult(
            updatedContext = updatedContext,
            extractedCandidates = extractedCandidates,
            replyMessage = reply,
            firewallDecisionId = firewallTaskId
        )
    }
}
