package com.mertsgi.moodify.data

import com.mertsgi.moodify.model.*

object SeedData {
    val user = UserProfile(
        id = "usr_alex_chen_92",
        name = "Alex Chen",
        preferredName = "Alex",
        role = "Product Designer",
        timezone = "America/Chicago",
        joinedDate = "2026-08-15"
    )

    val initialContext = ContextSnapshot(
        id = "ctx_snapshot_01",
        dimensions = ContextualDimensions(
            valence = 0.1f,
            energy = 0.35f,
            stress = 0.65f,
            socialNeed = 0.25f,
            focusNeed = 0.2f,
            noveltyNeed = 0.3f,
            confidence = 0.88f
        ),
        primaryState = "LOW_BATTERY",
        secondaryState = "Cognitive fatigue after 4-hour design review",
        userIntent = "Decompress without having to make complicated decisions",
        contextSource = "CONVERSATION_INFERRED",
        timestamp = "2026-09-28T19:30:00Z",
        timeOfDay = "EVENING",
        dayOfWeek = "Friday",
        weatherSummary = "62°F, overcast with gentle rain",
        freeHoursRemainingToday = 4.5f,
        notes = "Mock calendar fixture confirms no evening commitments past 5:30 PM."
    )

    val seedMemories = listOf(
        MemoryItem(
            id = "mem_01",
            category = "identity",
            key = "profession",
            value = "Staff Product Designer at a collaborative software studio",
            source = "USER_STATED",
            confidence = 0.98f,
            sensitivity = SensitivityLevel.NORMAL,
            isImportant = true,
            reasoningForBelief = "Directly stated in introductory chat."
        ),
        MemoryItem(
            id = "mem_02",
            category = "routines",
            key = "morning_run",
            value = "Runs 5k-8k on Tuesday and Saturday mornings along the lakefront",
            source = "CONVERSATION_EXTRACTED",
            sourceQuote = "I usually do my lakefront run early Tuesday and Saturday before standup.",
            confidence = 0.92f,
            sensitivity = SensitivityLevel.PERSONAL,
            allowedForExternalTools = false,
            reasoningForBelief = "Mentioned across multiple weekly check-ins."
        ),
        MemoryItem(
            id = "mem_03",
            category = "dislikes",
            key = "food_texture",
            value = "Strong dislike for overly sugary processed snacks; prefers savory, high-protein or crunchy roasted nuts",
            source = "USER_STATED",
            sourceQuote = "Please no sugary candy bars, I crash so hard in the afternoon. Need savory high protein stuff.",
            confidence = 0.95f,
            sensitivity = SensitivityLevel.NORMAL,
            isImportant = true,
            reasoningForBelief = "User explicit directive during grocery recommendation."
        ),
        MemoryItem(
            id = "mem_04",
            category = "budget_preferences",
            key = "workday_food_budget",
            value = "Prefers keeping weekday snacks and lunch under $20-$25 to stay within monthly budget",
            source = "USER_STATED",
            sourceQuote = "I try to keep casual work snack hauls under 25 bucks.",
            confidence = 0.90f,
            sensitivity = SensitivityLevel.NORMAL,
            isImportant = false
        ),
        MemoryItem(
            id = "mem_05",
            category = "food",
            key = "ramen_preference",
            value = "Loves rich tonkotsu or spicy sesame ramen, especially in small authentic counter spots",
            source = "CONVERSATION_EXTRACTED",
            sourceQuote = "Nothing fixes a rainy week like a steamy bowl of spicy sesame ramen at an authentic counter.",
            confidence = 0.94f,
            sensitivity = SensitivityLevel.NORMAL
        ),
        MemoryItem(
            id = "mem_06",
            category = "music",
            key = "ambient_focus",
            value = "Listens to ambient electronic and modern minimalism (Brian Eno, Jon Hopkins, Nils Frahm) during deep focus",
            source = "CONVERSATION_EXTRACTED",
            confidence = 0.96f,
            sensitivity = SensitivityLevel.NORMAL,
            isImportant = true
        ),
        MemoryItem(
            id = "mem_07",
            category = "music",
            key = "live_shows_preference",
            value = "Passionate about indie rock, bedroom pop, and math rock in intimate venues (Empty Bottle, Thalia Hall)",
            source = "USER_STATED",
            sourceQuote = "I hate stadium arenas. Intimate 500-cap rooms with great acoustics are where music feels real.",
            confidence = 0.97f,
            sensitivity = SensitivityLevel.NORMAL,
            isImportant = true
        ),
        MemoryItem(
            id = "mem_08",
            category = "movies_tv",
            key = "comfort_cinema",
            value = "When exhausted, enjoys cozy Japanese cinema (Kore-eda), slow cinema, or smart witty comedy (Fleabag, Severance)",
            source = "USER_STATED",
            confidence = 0.91f,
            sensitivity = SensitivityLevel.NORMAL,
            allowedForExternalTools = false
        ),
        MemoryItem(
            id = "mem_09",
            category = "relationships",
            key = "partner_maya",
            value = "Partner Maya is an architect who loves modern mid-century design and vegetarian cooking",
            source = "USER_STATED",
            confidence = 0.95f,
            sensitivity = SensitivityLevel.PERSONAL,
            allowedForExternalTools = false,
            reasoningForBelief = "Mentioned when searching for weekend day-trip ideas."
        ),
        MemoryItem(
            id = "mem_10",
            category = "work",
            key = "design_review_marcus",
            value = "Had a critical quarterly design review with VP Marcus today at 2:00 PM regarding mobile workspace launch",
            source = "CALENDAR_DERIVED",
            sourceQuote = "Mock calendar: 'Q3 Core Design Review w/ Marcus (VP Design)'",
            confidence = 0.96f,
            sensitivity = SensitivityLevel.PERSONAL,
            isImportant = true,
            allowedForExternalTools = false
        ),
        MemoryItem(
            id = "mem_11",
            category = "boundaries",
            key = "quiet_evening_rule",
            value = "Never wants high-energy social notifications or work reminders after 9:00 PM on weeknights",
            source = "USER_STATED",
            sourceQuote = "After 9pm I am off the grid. Do not buzz me about productivity or huge crowds.",
            confidence = 1.0f,
            sensitivity = SensitivityLevel.SENSITIVE,
            isImportant = true,
            allowedForExternalTools = false
        ),
        MemoryItem(
            id = "mem_12",
            category = "life_events",
            key = "family_health_care",
            value = "Mom recovering from knee surgery in Portland; checking in with brother every Sunday",
            source = "CONVERSATION_EXTRACTED",
            sourceQuote = "Mom is doing physical therapy in Portland this month.",
            confidence = 0.89f,
            sensitivity = SensitivityLevel.HIGHLY_SENSITIVE,
            allowedForPersonalization = false,
            allowedForExternalTools = false,
            reasoningForBelief = "Private family context. Protected strictly under Highly Sensitive."
        )
    )

    val seedTasteNodes = listOf(
        TasteNode("tn_01", "Japanese Breakfast", "artists", TasteRelation.LOVES, 0.96f, "Indie rock / shoegaze, loves intimate live gigs"),
        TasteNode("tn_02", "Jon Hopkins", "artists", TasteRelation.LOVES, 0.92f, "Ambient / IDM, great for decompression"),
        TasteNode("tn_03", "The National", "artists", TasteRelation.LIKES, 0.85f, "Melancholic indie rock"),
        TasteNode("tn_04", "Hirokazu Kore-eda", "directors", TasteRelation.LOVES, 0.94f, "Gentle humane cinema (After the Storm, Shoplifters)"),
        TasteNode("tn_05", "Severance", "series", TasteRelation.LOVES, 0.95f, "Dystopian workplace thriller / high design aesthetic"),
        TasteNode("tn_06", "Tonkotsu & Spicy Sesame Ramen", "cuisines", TasteRelation.LOVES, 0.93f, "Counter dining comfort food"),
        TasteNode("tn_07", "Thalia Hall Chicago", "venues", TasteRelation.LOVES, 0.97f, "Historic intimate music venue"),
        TasteNode("tn_08", "Sugary Candy & Sweet Cocktails", "flavors", TasteRelation.AVOIDS, 0.90f, "Hates energy crashes & saccharine drinks")
    )

    val seedRecommendations = listOf(
        RecommendationItem(
            id = "rec_01",
            title = "Jon Hopkins — Music for Psychedelic Therapy",
            subtitle = "Gentle, non-intrusive ambient soundscape (60 min)",
            category = "music",
            domain = "music",
            description = "A delicate ambient electronic record composed in the Ecuadorian caves. Zero jarring beats, soft acoustic textures, designed to ease cognitive overload.",
            badge = "Zero Mental Friction",
            metadata = RecommendationMetadata(duration = "60 mins", genreOrCuisine = "Ambient / Meditative", rating = "98% match"),
            whyThis = WhyThisExplanation(
                summary = "Tailored for your current cognitive depletion (stress 0.65, energy 0.35) and your love for Jon Hopkins and modern ambient minimalism.",
                matchedMemories = listOf(
                    MatchedMemoryRef("ambient_focus", "Prefers ambient electronic minimalism to decompress", "music"),
                    MatchedMemoryRef("profession", "Cognitive fatigue from intense visual/design work", "identity")
                ),
                contextAlignment = listOf(
                    ContextAlignmentRef("Energy (0.35)", "Demands zero active decision-making or loud hooks"),
                    ContextAlignmentRef("Stress (0.65)", "Low BPM downregulates sympathetic nervous response")
                ),
                tasteFactor = listOf(
                    TasteFactorRef("Jon Hopkins", "LOVES", "Primary anchor artist in taste graph")
                ),
                constraintsRespected = listOf("No loud drop", "Fits current evening hours", "Zero sensory clutter"),
                noveltyScore = 0.25f
            ),
            actionPrompt = "Play Ambient Sound",
            actionType = "LISTEN_NOW",
            score = 0.96f
        ),
        RecommendationItem(
            id = "rec_02",
            title = "Kore-eda's \"After the Storm\"",
            subtitle = "A quiet, tender, slightly funny Japanese evening film",
            category = "movies_tv",
            domain = "movies",
            description = "A washed-up novelist and private detective takes refuge from a typhoon in his elderly mother's small apartment. Low-stakes, deeply human, warm humor.",
            badge = "Low Effort Watch",
            metadata = RecommendationMetadata(duration = "1h 55m", genreOrCuisine = "Quiet Drama / Gentle Comedy", rating = "96% on RT", effortLevel = "LOW"),
            whyThis = WhyThisExplanation(
                summary = "You specified you love Kore-eda and gentle slow cinema when exhausted, without feeling boring.",
                matchedMemories = listOf(
                    MatchedMemoryRef("comfort_cinema", "Loves Kore-eda and Japanese cinema when battery is drained", "movies_tv")
                ),
                contextAlignment = listOf(
                    ContextAlignmentRef("Focus Need (0.2)", "Doesn't require keeping track of complex subplots"),
                    ContextAlignmentRef("Free Time (4.5h)", "Comfortably finishes before wind-down")
                ),
                tasteFactor = listOf(
                    TasteFactorRef("Hirokazu Kore-eda", "LOVES", "Direct thematic match in taste graph")
                ),
                constraintsRespected = listOf("Streaming available on Criterion/Apple TV", "Within evening bedtime boundaries"),
                noveltyScore = 0.35f
            ),
            actionPrompt = "Add to Watch Later",
            actionType = "WATCH_LATER",
            score = 0.94f
        ),
        RecommendationItem(
            id = "rec_03",
            title = "Japanese Breakfast — Secret Show",
            subtitle = "Intimate 450-cap room at Thalia Hall (Friday Oct 9)",
            category = "events",
            domain = "events",
            description = "Michelle Zauner announced an intimate acoustic/indie warm-up show before her international festival tour. Presale opens tomorrow at 10 AM.",
            badge = "Concert Radar",
            metadata = RecommendationMetadata(location = "Thalia Hall, Chicago", duration = "Oct 9, 8:00 PM", price = "$38.00", effortLevel = "ACTIVE"),
            whyThis = WhyThisExplanation(
                summary = "Japanese Breakfast is in your Top 3 artists and you specifically told Moodify you hate arenas and love 500-capacity historic rooms.",
                matchedMemories = listOf(
                    MatchedMemoryRef("live_shows_preference", "Intimate 500-cap rooms with great acoustics only", "music")
                ),
                contextAlignment = listOf(
                    ContextAlignmentRef("Friday Availability", "Mock calendar schedule shows Oct 9 evening is currently open")
                ),
                tasteFactor = listOf(
                    TasteFactorRef("Japanese Breakfast", "LOVES", "Highest affinity indie rock entity"),
                    TasteFactorRef("Thalia Hall", "LOVES", "Matches preferred venue profile")
                ),
                constraintsRespected = listOf("Sub-500 capacity room", "Fair ticket pricing ($38 vs scalpers)", "Mock calendar slot free"),
                noveltyScore = 0.6f
            ),
            actionPrompt = "Set Ticket Drop Reminder",
            actionType = "ADD_CALENDAR",
            score = 0.98f
        ),
        RecommendationItem(
            id = "rec_04",
            title = "Workday Savory Snack Haul (Under $22)",
            subtitle = "Tamari roasted almonds, crispy edamame & sparkling yuzu",
            category = "products",
            domain = "products",
            description = "Hand-picked three high-protein, zero-sugar afternoon crash-proof snacks that deliver crunch and sustained focus without the sugar crash.",
            badge = "Under $25 Budget",
            metadata = RecommendationMetadata(price = "$19.40 total (3 items)", location = "Whole Foods / Trader Joe's", effortLevel = "VERY_LOW"),
            whyThis = WhyThisExplanation(
                summary = "Directly follows your budget constraint (<$25) and your explicit rule: high savory protein, no sugar spikes.",
                matchedMemories = listOf(
                    MatchedMemoryRef("food_texture", "Hates sugary processed snacks; needs savory high-protein crunch", "dislikes"),
                    MatchedMemoryRef("workday_food_budget", "Keep workday snacks under $20-$25", "budget_preferences")
                ),
                contextAlignment = listOf(
                    ContextAlignmentRef("Energy (0.35)", "Solves the 3 PM afternoon slump without insulin spikes")
                ),
                tasteFactor = listOf(
                    TasteFactorRef("Roasted Almonds & Edamame", "LOVES", "Exact taste match"),
                    TasteFactorRef("Sugary Candy", "AVOIDS", "Eliminated all high-sugar items")
                ),
                constraintsRespected = listOf("Total: $19.40 (under $25 limit)", "High protein >14g per serving", "Dairy-free & clean ingredients"),
                noveltyScore = 0.4f
            ),
            actionPrompt = "Save to Snack Grocery List",
            actionType = "CREATE_LIST",
            score = 0.97f
        ),
        RecommendationItem(
            id = "rec_05",
            title = "Menya Goku — Counter Tonkotsu",
            subtitle = "10-seat neighborhood ramen counter with rich black garlic broth",
            category = "food",
            domain = "food",
            description = "Unassuming 10-stool counter tucked two blocks off the main avenue. Steamy broth simmered 18 hours with hand-pulled wavy noodles.",
            badge = "Neighborhood Gem",
            metadata = RecommendationMetadata(location = "Menya Goku (1.2 mi)", price = "$16 - $19", rating = "4.8 (340 reviews)", effortLevel = "LOW"),
            whyThis = WhyThisExplanation(
                summary = "Matches your desire for authentic intimate counter ramen on a cool overcast evening, without a 2-hour wait.",
                matchedMemories = listOf(
                    MatchedMemoryRef("ramen_preference", "Loves rich tonkotsu or spicy sesame ramen at small authentic counters", "food")
                ),
                contextAlignment = listOf(
                    ContextAlignmentRef("Weather (62° Overcast)", "Warm restorative broth pairs with damp evening"),
                    ContextAlignmentRef("Energy (0.35)", "Fast counter service, no reservations hassle")
                ),
                tasteFactor = listOf(
                    TasteFactorRef("Tonkotsu & Spicy Sesame Ramen", "LOVES", "Anchor comfort food entity")
                ),
                constraintsRespected = listOf("Under 20 minutes from home", "Solo-dining friendly counter", "Open until 10:30 PM"),
                noveltyScore = 0.2f
            ),
            actionPrompt = "Add to Dinner Plan",
            actionType = "SAVE_PLAN",
            score = 0.95f
        )
    )

    val seedPlans = listOf(
        PlanItem(
            id = "plan_01",
            title = "Quiet Evening Unwind Routine",
            type = "ACTIVITY",
            category = "Evening Reset",
            notes = "Dim lamps, tea, ambient music, no laptop past 9pm",
            status = "PENDING",
            date = "Tonight, 8:30 PM",
            venueOrPlatform = "Home"
        ),
        PlanItem(
            id = "plan_02",
            title = "After the Storm (Hirokazu Kore-eda)",
            type = "WATCH_LATER",
            category = "Cinema",
            notes = "Criterion Channel / Apple TV. Gentle Japanese drama.",
            status = "PENDING",
            venueOrPlatform = "Living Room TV",
            associatedRecommendationId = "rec_02"
        ),
        PlanItem(
            id = "plan_03",
            title = "Studio Snack Supply Haul",
            type = "SHOPPING_LIST",
            category = "Groceries",
            notes = "Under $25 budget constraint respected",
            status = "PENDING",
            items = listOf(
                PlanSubItem("sub_01", "Tamari dry-roasted almonds (8oz)", true),
                PlanSubItem("sub_02", "Crispy sea salt roasted edamame", false),
                PlanSubItem("sub_03", "Sparkling unsweetened yuzu water (4-pack)", false)
            )
        )
    )

    val seedActions = listOf(
        ActionPlan(
            id = "act_01",
            title = "Hold Ticket Drop Hold on Calendar",
            description = "Block 15-minute simulated hold for Japanese Breakfast presale on Thursday at 9:55 AM (local prototype simulation).",
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
    )

    val seedIntegrations = listOf(
        IntegrationProvider("int_01", "Google Calendar", "Productivity", "Monitors schedule density and free blocks to detect cognitive load.", "Calendar", IntegrationStatus.MOCK, "READ_ONLY", null),
        IntegrationProvider("int_02", "Spotify", "Entertainment", "Syncs recently played tracks and audio features to update music taste.", "Music", IntegrationStatus.MOCK, "READ_WRITE", null),
        IntegrationProvider("int_03", "Todoist", "Tasks", "Provides insight into upcoming deadlines and task volume.", "CheckSquare", IntegrationStatus.NOT_CONNECTED, "READ_ONLY", null),
        IntegrationProvider("int_04", "Apple Health / Sleep", "Wellbeing", "Understands sleep debt and HRV recovery to adjust energy dimensions.", "Activity", IntegrationStatus.MOCK, "READ_ONLY", null),
        IntegrationProvider("int_05", "Google Maps", "Places", "Detects favorite neighborhood cafes, transit patterns, and travel times.", "MapPin", IntegrationStatus.MOCK, "READ_ONLY", null)
    )

    val initialChatMessages = listOf(
        ChatMessage(
            id = "msg_01",
            sender = MessageSender.ASSISTANT,
            text = "Good evening Alex. I noticed your 4-hour mobile design review just wrapped up. Your energy is running around 35% with elevated cognitive load.\n\nI’ve suppressed loud notifications and queued up three low-effort resets for tonight. How are you feeling right now?",
            timestamp = "2026-09-28T18:00:00Z",
            suggestedReplies = listOf(
                "Today was awful. I don't really want to think.",
                "I have no plans this Friday night. What should I do?",
                "Pick something to watch tonight. Tired but don't want boring."
            )
        )
    )

    val defaultPrivacySettings = PrivacySettings(
        isPrivateSession = false,
        maxAllowedSensitivity = SensitivityLevel.PERSONAL,
        allowProactiveSuggestions = true,
        allowExternalIntegrations = true,
        logAllDecisions = true
    )

    val defaultProactiveSettings = ProactiveSetting(
        enabled = true,
        mode = ProactiveMode.BALANCED,
        userConfiguredMax = 3,
        quietHoursStart = "22:00",
        quietHoursEnd = "08:00",
        respectLowBatteryState = true
    )
}
