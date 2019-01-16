import { CardDescription } from "./CardDescription";

export class Card {
  public readonly iconCssClasses: string[];
  public readonly title: string;
  public readonly description: CardDescription;
  public readonly perspectiveId: string;
  public readonly onMayClick?: () => boolean;

  constructor(
    iconCssClasses: string[],
    title: string,
    description: CardDescription,
    perspectiveId: string,
    onMayClick?: () => boolean
  ) {
    this.iconCssClasses = iconCssClasses;
    this.title = title;
    this.description = description;
    this.perspectiveId = perspectiveId;
    this.onMayClick = onMayClick;
  }
}
