export class CardDescriptionElement {
  public isText(): this is CardDescriptionTextElement {
    return this instanceof CardDescriptionTextElement;
  }

  public isLink(): this is CardDescriptionLinkElement {
    return this instanceof CardDescriptionLinkElement;
  }
}

export class CardDescriptionTextElement extends CardDescriptionElement {
  public readonly text: string;

  constructor(text: string) {
    super();
    this.text = text;
  }
}

export class CardDescriptionLinkElement extends CardDescriptionElement {
  public readonly text: string;
  public readonly targetId: string;

  constructor(text: string, targetId: string) {
    super();
    this.text = text;
    this.targetId = targetId;
  }
}

export class CardDescription {
  public readonly elements: CardDescriptionElement[];

  constructor(elements: CardDescriptionElement[]) {
    this.elements = elements;
  }
}
