import * as React from "react";
import {Popup} from "./Popup";
import {OrganizationalUnitService} from "@kiegroup-ts-generated/uberfire-structure-api-rpc";

interface Props {
    onClose: () => void
    organizationalUnitService: OrganizationalUnitService,
}

interface State {
    name: string;
    errors: string[];
    displayErrors: string[];
}

export class NewSpacePopup extends React.Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = {name: "", errors: [], displayErrors: []};
    }

    private add() {

        const newSpace = {
            name: this.state.name,
            owner: "admin", //FIXME: get logged user
            defaultGroupId: `com.${this.state.name.toLowerCase()}`
        };

        const emptyName = Promise.resolve()
            .then(() => {
                if (!newSpace.name || newSpace.name.trim() === "") {
                    this.setState(prevState => ({errors: [...prevState.errors, `The field "Name" should not be empty.`]}));
                    return Promise.reject();
                } else {
                    return Promise.resolve();
                }
            });

        const duplicatedName = Promise.resolve()
            .then(() => this.props.organizationalUnitService.getOrganizationalUnit({name: newSpace.name}))
            .then(space => {
                if (space) {
                    this.setState(prevState => ({errors: [...prevState.errors, "A Space with the same name already exists"]}));
                    return Promise.reject();
                } else {
                    return Promise.resolve();
                }
            });

        const validGroupId = Promise.resolve()
            .then(() => this.props.organizationalUnitService.isValidGroupId({proposedGroupId: newSpace.defaultGroupId}))
            .then(valid => {
                if (!valid) {
                    this.setState(prevState => ({errors: [...prevState.errors, "A space's name can only contain letters (A to Z), digits (0 to 9), underscores (_), dots (.) or dashes (-)"]}));
                    return Promise.reject();
                } else {
                    return Promise.resolve();
                }
            });

        this.setState({errors: []}, () => Promise.resolve()
            .then(() => Promise.all([emptyName, duplicatedName, validGroupId]))
            .then(() => this.props.organizationalUnitService.createOrganizationalUnit0(newSpace))
            .then(i => this.props.onClose())
            .catch(() => this.setState(prevState => ({displayErrors: prevState.errors}))));
    }

    render() {
        return <>

        <Popup onClose={() => this.props.onClose()}>
            <div className={"modal-content"}>
                <div className={"modal-header"}>
                    <button type="button" className={"close"} data-dismiss="modal" onClick={() => this.props.onClose()}>×</button>
                    <h4 className={"modal-title"}>Add Space</h4>
                </div>
                <div className={"modal-body"}>
                    {this.state.displayErrors.map(err =>
                        <div className={"alert alert-danger alert-dismissable"}>
                            <span className={"pficon pficon-error-circle-o"}/>
                            <span>{err}</span>
                        </div>
                    )}
                    <label className={"form-control-label required"}>Name</label>
                    <div className={"form-group"}>
                        <input type={"text"} className={"form-control"} onInput={(e: any) => this.setState({name: e.target.value})}/>
                    </div>
                </div>
                <div className={"modal-footer"}>
                    <button className={"btn btn-default"} onClick={() => this.props.onClose()}>Cancel</button>
                    <button className={"btn btn-primary"} onClick={() => this.add()}>Add</button>
                </div>
            </div>
        </Popup>

        </>;
    }
}