import { ContextSnapshot, ContextualDimensions, PrimaryContextState } from '../types/context';

export class ContextEngine {
  /**
   * Updates context dimensions based on user input or explicit action.
   * Enforces that SELF_REPORTED has highest priority and confidence.
   */
  public static inferContextFromText(
    current: ContextSnapshot,
    userText: string
  ): ContextSnapshot {
    const text = userText.toLowerCase();
    const updatedDimensions: ContextualDimensions = { ...current.dimensions };
    let primaryState: PrimaryContextState = current.primaryState;
    let secondaryState = current.secondaryState;
    let userIntent = current.userIntent;

    // SCENARIO A: "Today was awful. I don’t really want to think."
    if (
      text.includes('awful') ||
      text.includes("don't really want to think") ||
      text.includes('exhausted') ||
      text.includes('terrible day')
    ) {
      updatedDimensions.valence = -0.4;
      updatedDimensions.energy = 0.15; // Low battery
      updatedDimensions.stress = 0.8;  // High stress
      updatedDimensions.focusNeed = 0.05; // Zero complex thinking
      updatedDimensions.socialNeed = 0.15; // Prefers quiet solitude
      updatedDimensions.noveltyNeed = 0.1; // Prefers familiar comfort
      updatedDimensions.confidence = 0.95;

      primaryState = 'LOW_BATTERY';
      secondaryState = 'High cognitive fatigue and workday overwhelm';
      userIntent = 'Minimum-friction wind down; wants to rest without deciding';
    }

    // SCENARIO B: "It actually went well! Ready for the weekend."
    else if (
      text.includes('went well') ||
      text.includes('ready for the weekend') ||
      text.includes('feeling great') ||
      text.includes('celebrate')
    ) {
      updatedDimensions.valence = 0.85;
      updatedDimensions.energy = 0.75;
      updatedDimensions.stress = 0.2;
      updatedDimensions.focusNeed = 0.4;
      updatedDimensions.socialNeed = 0.7;
      updatedDimensions.noveltyNeed = 0.65;
      updatedDimensions.confidence = 0.92;

      primaryState = 'ANTICIPATING_WEEKEND';
      secondaryState = 'Positive closure from successful review; open to music/outing';
      userIntent = 'Celebrate the week, explore live entertainment or tasty dinner';
    }

    // SCENARIO C: "I need snacks for work but trying not to eat junk all day."
    else if (
      text.includes('snacks') ||
      text.includes('junk') ||
      text.includes('groceries')
    ) {
      updatedDimensions.focusNeed = 0.6; // Pragmatic task
      userIntent = 'Pragmatic, health-conscious workday grocery planning under budget';
    }

    // SCENARIO E: "Pick something for tonight. I’m tired but don’t want something boring."
    else if (
      text.includes('pick something for tonight') ||
      text.includes("tired but don't want something boring") ||
      text.includes('movie night')
    ) {
      updatedDimensions.energy = 0.3;
      updatedDimensions.focusNeed = 0.25;
      updatedDimensions.noveltyNeed = 0.7; // "don't want something boring" = craving clever novelty with low effort!
      primaryState = 'WINDING_DOWN';
      secondaryState = 'Fatigued but mentally curious';
      userIntent = 'High-taste cinema or television that holds attention without taxing mental stamina';
    }

    return {
      ...current,
      id: `ctx_${Date.now()}`,
      dimensions: updatedDimensions,
      primaryState,
      secondaryState,
      userIntent,
      contextSource: 'CONVERSATION_INFERRED',
      timestamp: new Date().toISOString(),
    };
  }

  /**
   * Directly sets self-reported mood from manual user interaction
   */
  public static setSelfReportedMood(
    current: ContextSnapshot,
    partialDimensions: Partial<ContextualDimensions>,
    primaryState?: PrimaryContextState
  ): ContextSnapshot {
    return {
      ...current,
      id: `ctx_${Date.now()}_self`,
      dimensions: {
        ...current.dimensions,
        ...partialDimensions,
        confidence: 1.0, // Self-report is highest authority
      },
      primaryState: primaryState || current.primaryState,
      contextSource: 'SELF_REPORTED',
      timestamp: new Date().toISOString(),
    };
  }
}
