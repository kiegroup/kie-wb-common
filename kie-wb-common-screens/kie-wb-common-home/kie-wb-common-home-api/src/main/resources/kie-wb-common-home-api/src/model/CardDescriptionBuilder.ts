import {
  CardDescription,
  CardDescriptionElement,
  CardDescriptionLinkElement,
  CardDescriptionTextElement
} from "./CardDescription";

export class CardDescriptionBuilder {
  private static readonly linkTokenRegex = new RegExp(/{(\d+)}/g);

  private readonly textMask: string;
  private readonly links: CardDescriptionLinkElement[];

  constructor(textMask: string) {
    this.textMask = textMask;
    this.links = [];
  }

  public addLinkIf(predicate: () => boolean, text: string, targetId: string): CardDescriptionBuilder {
    if (!predicate()) {
      return this;
    }
    return this.addLink(text, targetId);
  }

  public addLink(text: string, targetId: string): CardDescriptionBuilder {
    this.links.push(new CardDescriptionLinkElement(text, targetId));
    return this;
  }

  public build(): CardDescription {
    const linkTokens = this.textMask.match(CardDescriptionBuilder.linkTokenRegex);

    if (linkTokens === null || linkTokens.length === 0) {
      return new CardDescription([new CardDescriptionTextElement(this.textMask)]);
    }

    const elements: CardDescriptionElement[] = [];
    for (let i = 0; i < linkTokens.length; i++) {
      elements.push(new CardDescriptionTextElement(this.getTextUntilLinkAt(this.textMask, linkTokens, i)));
      elements.push(this.links[this.linkIdxFromToken(linkTokens[i])]);
    }

    const lastElement = new CardDescriptionTextElement(
      this.getTextUntilLinkAt(this.textMask, linkTokens, linkTokens.length)
    );
    elements.push(lastElement);

    return new CardDescription(elements);
  }

  private getTextUntilLinkAt(textMask: string, tokens: RegExpMatchArray, i: number) {
    const prevLinkIdx = i === 0 ? 0 : this.linkIdxFromToken(tokens[i - 1]);
    const prevLinkTkn = `{${prevLinkIdx}}`;

    const textTokenStart = i === 0 ? 0 : textMask.indexOf(prevLinkTkn) + prevLinkTkn.length;
    const textTokenEnd = i === tokens.length ? textMask.length : textMask.indexOf(tokens[i]);

    return textMask.substring(textTokenStart, textTokenEnd);
  }

  private linkIdxFromToken(tkn: string) {
    return parseInt(tkn.replace("{", "").replace("}", ""), 10);
  }
}
