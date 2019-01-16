import * as React from "react";
import * as AppFormer from "appformer-js";
import {CardDescription, CardDescriptionLinkElement, CardDescriptionTextElement} from "../model";
import {AuthorizationManager} from "../util";

interface Props {
    description: CardDescription;
}

export class CardDescriptionView extends React.Component<Props, {}> {

    constructor(props: Props) {
        super(props);

        this.state = {};
    }

    public render() {
        return <>
            {this.props.description.elements.map((element, idx) => {
                if (element.isText()) {
                    return <TextElement model={element} key={idx}/>;
                }

                if (element.isLink()) {
                    return <LinkElement model={element} hasAccess={this.hasAccessToLink(element.targetId)} key={idx}/>;
                }
            })}
        </>;
    }

    private hasAccessToLink(targetId: string): boolean {
        return AuthorizationManager.hasAccessToPerspective(targetId);
    }
}

function TextElement(props: { model: CardDescriptionTextElement }) {
    return <span data-field="text">{props.model.text}</span>;
}

function LinkElement(props: { model: CardDescriptionLinkElement; hasAccess: boolean }) {

    const disabledClass = props.hasAccess ? "" : "disabled";
    const onClickFunc = props.hasAccess ? (e: React.MouseEvent<HTMLElement>) => {
        e.stopPropagation();
        AppFormer.goTo(props.model.targetId);
    } : undefined;

    return <a data-field="link" className={`kie-hero-card__link ${disabledClass}`} onClick={onClickFunc}>{props.model.text}</a>;
}
