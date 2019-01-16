import * as React from "react";
import { CardDescriptionView } from "./CardDescriptionView";
import { Card } from "../model";
import { AuthorizationManager } from "../util";
import * as AppFormer from "appformer-js";

interface Props {
  model: Card;
}

export class CardView extends React.Component<Props, {}> {
  constructor(props: Props) {
    super(props);
    this.state = {};
  }

  public render() {
    const onClickFunc = this.buildOnClickFunction();
    const cardId = `home-action-${this.props.model.title.toLowerCase()}`;
    const iconClasses = this.props.model.iconCssClasses.join(" ");

    return (
      <div data-field="card" className="kie-hero-card" role="button" onClick={onClickFunc} id={cardId}>
        <div data-field="icon" className={`kie-hero-card__icon kie-circle-icon kie-circle-icon--lg ${iconClasses}`} />
        <div className="kie-hero-card__text">
          <h2 data-field="heading">{this.props.model.title}</h2>

          <CardDescriptionView description={this.props.model.description} />
        </div>
      </div>
    );
  }

  private buildOnClickFunction(): (() => void) | undefined {
    if (!this.hasAccessToCard()) {
      return undefined;
    }

    return () => {
      if (!this.props.model.onMayClick || this.props.model.onMayClick()) {
        AppFormer.goTo(this.props.model.perspectiveId);
      }
    };
  }

  private hasAccessToCard(): boolean {
    return AuthorizationManager.hasAccessToPerspective(this.props.model.perspectiveId);
  }
}
