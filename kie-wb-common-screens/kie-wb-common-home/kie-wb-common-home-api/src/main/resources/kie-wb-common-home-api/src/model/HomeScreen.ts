import { Card } from "./Card";

export class HomeScreen {
  public readonly welcomeText: string;
  public readonly description: string;
  public readonly backgroundImageUrl: string;
  public readonly cards: Card[];

  constructor(welcomeText: string, description: string, backgroundImageUrl: string, cards: Card[]) {
    this.welcomeText = welcomeText;
    this.description = description;
    this.backgroundImageUrl = backgroundImageUrl;
    this.cards = cards;
  }
}
