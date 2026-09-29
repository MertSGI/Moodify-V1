import { ChatMessage, ChatCardPayload } from '../types/chat';
import { ContextSnapshot } from '../types/context';
import { MemoryItem, CandidateMemory } from '../types/memory';
import { TasteNode } from '../types/taste';
import { RecommendationItem } from '../types/recommendation';
import { ActionPlan } from '../types/actions';
import { PrivacySettings } from '../types/privacy';
import { PersonalContextFirewall } from './firewallService';
import { MemoryService } from './memoryService';
import { ContextEngine } from './contextService';

export interface AgentProcessingResult {
  updatedContext: ContextSnapshot;
  extractedCandidates: CandidateMemory[];
  replyMessage: ChatMessage;
  firewallDecisionId: string;
}

export class AgentOrchestrator {
  /**
   * Main agent loop executing:
   * 1. Context interpretation
   * 2. Candidate memory extraction
   * 3. Personal Context Firewall filtering
   * 4. Structured reasoning & response generation
   */
  public static async processUserMessage(
    userText: string,
    currentContext: ContextSnapshot,
    memories: MemoryItem[],
    tasteNodes: TasteNode[],
    allRecommendations: RecommendationItem[],
    privacySettings: PrivacySettings
  ): Promise<AgentProcessingResult> {
    const textLower = userText.toLowerCase();

    // 1. Context interpretation
    const updatedContext = ContextEngine.inferContextFromText(currentContext, userText);

    // 2. Candidate memory extraction (with tiering)
    const extractedCandidates = privacySettings.isPrivateSession
      ? [] // Private session disables durable extraction
      : MemoryService.extractCandidateMemories(userText);

    // 3. Personal Context Firewall
    const firewallResult = PersonalContextFirewall.filterContextForTask(
      `Respond to user message: "${userText.substring(0, 60)}"`,
      'RECOMMENDER',
      memories,
      privacySettings
    );

    const admittedMemories = firewallResult.admitted;
    const firewallTaskId = firewallResult.decision.taskId;

    // 4. Generate structured response with scenarios
    const cards: ChatCardPayload[] = [];
    let responseText = '';
    let suggestedReplies: string[] = [];

    // SCENARIO A: ROUGH DAY
    if (
      textLower.includes('awful') ||
      textLower.includes("don't really want to think") ||
      textLower.includes('exhausted')
    ) {
      responseText =
        "I hear you Alex. Let’s protect your evening. When your battery is drained like this, the worst thing is having to make decisions.\n\nI’ve picked three zero-friction ways to let your nervous system reset tonight—one quiet sound, one gentle watch, and one tiny offline reset. Just pick whatever requires the least effort.";

      const musicRec = allRecommendations.find(r => r.id === 'rec_01') || allRecommendations[0];
      const movieRec = allRecommendations.find(r => r.id === 'rec_02') || allRecommendations[1];

      // Tiny offline activity
      const activityRec: RecommendationItem = {
        id: 'rec_act_reset',
        title: 'Warm Shower + 15-Minute Foam Roll',
        subtitle: 'Zero screen engagement, physical reset',
        category: 'activities',
        domain: 'activities',
        description: 'No podcast, no bright screens. Dim lighting, hot water to ease shoulder tension, and gentle floor stretching.',
        badge: 'Offline Reset',
        metadata: {
          duration: '20 mins',
          effortLevel: 'VERY_LOW',
        },
        whyThis: {
          summary: 'Tailored for high tension and low battery (stress 0.8, energy 0.15) without needing mental effort.',
          matchedMemories: [
            { key: 'boundaries', snippet: 'Low cognitive load during evening recovery', domain: 'boundaries' },
          ],
          contextAlignment: [
            { dimension: 'Energy (0.15)', reason: 'Zero screens or decision-making required' },
          ],
          tasteFactor: [],
          constraintsRespected: ['No screens', 'Immediate home action'],
          noveltyScore: 0.1,
        },
        actionPrompt: 'Save to Evening Plan',
        actionType: 'SAVE_PLAN',
        score: 0.99,
      };

      cards.push({
        type: 'RECOMMENDATION',
        recommendations: [musicRec, movieRec, activityRec],
        title: '3 Low-Friction Options for Tonight',
      });

      suggestedReplies = [
        'The ambient sound sounds perfect.',
        'I’ll put on After the Storm.',
        'Just going to do the shower reset and head to sleep early.',
      ];
    }

    // SCENARIO B: FRIDAY NIGHT OUT / CONCERT
    else if (
      textLower.includes('friday') ||
      textLower.includes('ready for the weekend') ||
      textLower.includes('concert') ||
      textLower.includes('free tonight') ||
      textLower.includes('celebrate')
    ) {
      responseText =
        "Looking at your schedule snapshot, your Friday night is completely open. Because you love intimate indie rock shows and smaller venues like Thalia Hall, here is an incredible option for this month:";

      const concertRec = allRecommendations.find(r => r.id === 'rec_03') || allRecommendations[2];

      const calendarProposal: ActionPlan = {
        id: `act_${Date.now()}`,
        title: 'Schedule Ticket Drop Hold (Mock)',
        description: 'Set a 15-minute simulated hold for "Japanese Breakfast Ticket Drop" on Thursday at 9:55 AM (local prototype simulation).',
        targetProvider: 'CALENDAR',
        actionName: 'CREATE_CALENDAR_EVENT',
        riskLevel: 'EXTERNAL_WRITE',
        requiresExplicitConfirmation: true,
        parameters: [
          { name: 'summary', label: 'Event Title', value: 'Japanese Breakfast Presale Alert', type: 'string' },
          { name: 'start', label: 'Time', value: '2026-10-01T09:55:00', type: 'date' },
          { name: 'duration', label: 'Duration', value: '15 mins', type: 'string' },
        ],
        status: 'AWAITING_CONFIRMATION',
        createdAt: new Date().toISOString(),
      };

      cards.push({
        type: 'RECOMMENDATION',
        recommendations: [concertRec],
        title: 'Matched Live Show in Chicago',
      });

      cards.push({
        type: 'ACTION_PROPOSAL',
        actionPlan: calendarProposal,
        title: 'Proposed Action (Requires Confirmation)',
      });

      suggestedReplies = [
        'Authorize simulated calendar hold.',
        'Why did you recommend this specific venue?',
        'Show me something more low-key instead.',
      ];
    }

    // SCENARIO C: PRODUCT DISCOVERY (Snacks under budget)
    else if (
      textLower.includes('snack') ||
      textLower.includes('junk') ||
      textLower.includes('groceries')
    ) {
      responseText =
        "Remembering that you hate afternoon sugar crashes and prefer keeping weekday food hauls under $25, I put together this high-protein savory snack trio for your studio desk:";

      const snackRec = allRecommendations.find(r => r.id === 'rec_04') || allRecommendations[3];

      cards.push({
        type: 'RECOMMENDATION',
        recommendations: [snackRec],
        title: 'Workday Savory Snack Haul',
      });

      suggestedReplies = [
        'Add all three to my Shopping List in Plans.',
        'Can you swap the roasted almonds for pumpkin seeds?',
        'Show the budget breakdown.',
      ];
    }

    // SCENARIO C2: FOOD / INEXPENSIVE DINNER (Section 7 Firewall Test)
    else if (
      textLower.includes('inexpensive') ||
      textLower.includes('something to eat') ||
      textLower.includes('what to eat') ||
      textLower.includes('dinner tonight') ||
      textLower.includes('dinner') ||
      textLower.includes('comfort food') ||
      textLower.includes('favorite comfort food')
    ) {
      // Find food or comfort food preference from admitted memories
      const comfortFoodMem = admittedMemories.find(m => m.category === 'food');
      const budgetMem = admittedMemories.find(m => m.category === 'budget_preferences');

      const foodDetail = comfortFoodMem ? `your preference for ${comfortFoodMem.value}` : 'comfort food';
      const budgetDetail = budgetMem ? `keeping it under budget (${budgetMem.value})` : 'keeping it casual and inexpensive';

      responseText = `I checked your preferences through the Context Firewall (${admittedMemories.length} relevant food & budget constraints admitted, unrelated work, travel, and personal domains withheld).\n\nBased on ${foodDetail} and ${budgetDetail}, here is a warm, comforting dinner pick for tonight:`;

      const foodRec = allRecommendations.find(r => r.category === 'food' || r.id === 'rec_05') || allRecommendations[4];

      cards.push({
        type: 'RECOMMENDATION',
        recommendations: [foodRec],
        title: 'Comfort Dinner Match (Under Budget)',
      });

      suggestedReplies = [
        'Save this to my Evening Plan.',
        'Why did you recommend this specific spot?',
        'Show me the firewall context breakdown.',
      ];
    }

    // SCENARIO D: FOLLOW-UP ON DESIGN REVIEW
    else if (
      textLower.includes('marcus') ||
      textLower.includes('review') ||
      textLower.includes('went well') ||
      textLower.includes('interview')
    ) {
      responseText =
        "That’s great to hear, Alex. That mobile redesign was a huge milestone and you’d put a lot of thought into the interaction patterns. Since you’ve been running at high intensity all week, give yourself credit for getting that through approval.\n\nWant to celebrate with a good dinner or ease gently into the weekend?";

      const ramenRec = allRecommendations.find(r => r.id === 'rec_05') || allRecommendations[4];

      cards.push({
        type: 'RECOMMENDATION',
        recommendations: [ramenRec],
        title: 'Solo Comfort Celebration',
      });

      suggestedReplies = [
        'Ramen at Menya Goku sounds amazing right now.',
        'I just want a movie and couch tonight.',
        'What’s on my schedule tomorrow morning?',
      ];
    }

    // SCENARIO E: MOVIE NIGHT (3 Differentiated Choices)
    else if (
      textLower.includes('movie') ||
      textLower.includes('watch') ||
      textLower.includes('tired but don’t want something boring') ||
      textLower.includes('tired but dont want something boring')
    ) {
      responseText =
        "When you’re mentally tired but still want something sharp, you need films that are captivating without demanding spreadsheet-level focus. Here are 3 genuinely differentiated picks matching your taste:";

      const movieChoices: RecommendationItem[] = [
        allRecommendations.find(r => r.id === 'rec_02') || allRecommendations[1],
        {
          id: 'rec_mv_02',
          title: 'Severance (Season 1, Ep 1-3)',
          subtitle: 'Immaculate workplace mystery & dry dark satire',
          category: 'movies_tv',
          domain: 'series',
          description: 'Surgical design aesthetic, hypnotic pacing, and sharp commentary on corporate compartmentalization.',
          badge: 'High Taste Satire',
          imageUrl: 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=600&auto=format&fit=crop&q=80',
          metadata: {
            duration: '54m per episode',
            genreOrCuisine: 'Dystopian Thriller / Satire',
            rating: '97% match',
            effortLevel: 'LOW',
          },
          whyThis: {
            summary: 'Matches your design eye and affinity for smart workplace tension without loud sensory overload.',
            matchedMemories: [
              { key: 'profession', snippet: 'Appreciates high-production design and architecture in cinema', domain: 'identity' },
            ],
            contextAlignment: [
              { dimension: 'Novelty (0.7)', reason: 'Clever hooks that keep you intrigued without exhausting you' },
            ],
            tasteFactor: [
              { tasteNodeName: 'Severance', relation: 'LOVES', weightReason: 'Saved in your favorite series' },
            ],
            constraintsRespected: ['Available on Apple TV+', 'Episodic bite sizes'],
            noveltyScore: 0.45,
          },
          actionPrompt: 'Add to Watch Later',
          actionType: 'WATCH_LATER',
          score: 0.95,
        },
        {
          id: 'rec_mv_03',
          title: 'Past Lives (Celine Song)',
          subtitle: 'Poignant, beautifully shot romantic drama in NYC & Seoul',
          category: 'movies_tv',
          domain: 'movies',
          description: 'Two deeply connected childhood friends are reunited across two decades. Quiet, profound, emotionally restorative.',
          badge: 'Emotional Resonance',
          imageUrl: 'https://images.unsplash.com/photo-1485846234645-a62644f84728?w=600&auto=format&fit=crop&q=80',
          metadata: {
            duration: '1h 46m',
            genreOrCuisine: 'Quiet Drama',
            rating: '98% on Rotten Tomatoes',
            effortLevel: 'LOW',
          },
          whyThis: {
            summary: 'A thoughtful, warm cinematic experience with stunning photography that feels rich rather than disposable.',
            matchedMemories: [
              { key: 'comfort_cinema', snippet: 'Appreciates quiet, tender cinema', domain: 'movies_tv' },
            ],
            contextAlignment: [
              { dimension: 'Valence (0.1)', reason: 'Emotionally grounding without heavy cynicism' },
            ],
            tasteFactor: [],
            constraintsRespected: ['Sub-2 hour runtime', 'High critical consensus'],
            noveltyScore: 0.6,
          },
          actionPrompt: 'Add to Watch Later',
          actionType: 'WATCH_LATER',
          score: 0.92,
        },
      ];

      cards.push({
        type: 'RECOMMENDATION',
        recommendations: movieChoices,
        title: '3 Distinct Styles for Tonight',
      });

      suggestedReplies = [
        'Save After the Storm to my watch list.',
        'Severance sounds like the right balance of sharp and relaxing.',
        'Tell me why you picked Past Lives.',
      ];
    }

    // SCENARIO F: CONCERT WATCH / ARTIST RADAR
    else if (
      textLower.includes('japanese breakfast') ||
      textLower.includes('track') ||
      textLower.includes('tour')
    ) {
      responseText =
        "I have Japanese Breakfast on your live concert tracking radar. Here is the latest confirmed intimate date, plus Thalia Hall venue details:";

      const concertRec = allRecommendations.find(r => r.id === 'rec_03') || allRecommendations[2];

      cards.push({
        type: 'RECOMMENDATION',
        recommendations: [concertRec],
        title: 'Concert Radar Alert',
      });

      suggestedReplies = [
        'Set a calendar alert for the presale.',
        'Add to my Tracked Concerts list in Plans.',
        'Are there any other artists touring soon?',
      ];
    }

    // GENERAL CONVERSATION FALLBACK
    else {
      responseText = `I understand, Alex. Based on what you've shared with me so far, I’m keeping your current state in mind (low battery, evening hours, quiet recharge). How can I make tonight feel easier for you?`;

      suggestedReplies = [
        'Show me something easy to watch tonight.',
        'Find me a quiet ramen spot nearby.',
        'What do you know about my music taste?',
        'Explain the Personal Context Firewall.',
      ];
    }

    // If candidate memories were extracted, attach them to the reply so the user can inspect!
    const replyMessage: ChatMessage = {
      id: `msg_${Date.now()}`,
      sender: 'ASSISTANT',
      text: responseText,
      timestamp: new Date().toISOString(),
      suggestedReplies,
      cards: cards.length > 0 ? cards : undefined,
      extractedCandidateMemories: extractedCandidates.length > 0 ? extractedCandidates : undefined,
      firewallTaskId,
    };

    return {
      updatedContext,
      extractedCandidates,
      replyMessage,
      firewallDecisionId: firewallTaskId,
    };
  }
}
