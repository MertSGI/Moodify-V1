import { TasteNode, TasteDomain, TasteRelation } from '../types/taste';
import { RecommendationFeedbackType } from '../types/recommendation';

export class TasteGraphService {
  /**
   * Applies user recommendation feedback back into the Taste Graph
   */
  public static applyFeedback(
    currentNodes: TasteNode[],
    itemName: string,
    domain: TasteDomain,
    feedback: RecommendationFeedbackType
  ): TasteNode[] {
    const existingIndex = currentNodes.findIndex(
      n => n.name.toLowerCase() === itemName.toLowerCase() || n.name.toLowerCase().includes(itemName.toLowerCase())
    );

    let updatedNodes = [...currentNodes];
    const today = new Date().toISOString().split('T')[0];

    let newRelation: TasteRelation = 'LIKES';
    let newStrength = 0.8;

    switch (feedback) {
      case 'LOVE_IT':
        newRelation = 'LOVES';
        newStrength = 0.98;
        break;
      case 'LIKE_IT':
        newRelation = 'LIKES';
        newStrength = 0.82;
        break;
      case 'NOT_FOR_ME':
        newRelation = 'AVOIDS';
        newStrength = 0.9;
        break;
      case 'ALREADY_KNOW_IT':
      case 'ALREADY_TRIED_IT':
        newRelation = 'HAS_TRIED';
        newStrength = 0.85;
        break;
      case 'SAVE_FOR_LATER':
        newRelation = 'SAVED';
        newStrength = 0.88;
        break;
      case 'NOT_NOW':
        // Mild temporary dampening
        newStrength = 0.6;
        break;
    }

    if (existingIndex >= 0) {
      updatedNodes[existingIndex] = {
        ...updatedNodes[existingIndex],
        relation: newRelation,
        strength: newStrength,
        recency: today,
      };
    } else {
      updatedNodes.push({
        id: `tn_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`,
        name: itemName,
        domain,
        relation: newRelation,
        strength: newStrength,
        recency: today,
        tags: [domain, feedback.toLowerCase().replace('_', ' ')],
      });
    }

    return updatedNodes;
  }
}
