import * as React from "react";
import * as AppFormer from "appformer-js";
import {Popup} from "./Popup";
import {OrganizationalUnitService} from "@kiegroup-ts-generated/uberfire-structure-api-rpc";
import {AuthenticationService} from "@kiegroup-ts-generated/errai-security-server-rpc";
import {UserImpl} from "@kiegroup-ts-generated/errai-security-server";

interface Props {
    onClose: () => void
    organizationalUnitService: OrganizationalUnitService,
    authenticationService: AuthenticationService,
}

interface State {
    name: string;
    errorMessages: string[];
    displayedErrorMessages: string[];
}

export class NewSpacePopup extends React.Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = {name: "", errorMessages: [], displayedErrorMessages: []};
    }

    private async add() {

        const user = await this.props.authenticationService.getUser({});

        const newSpace = {
            name: this.state.name,
            owner: (user as UserImpl).name!,
            defaultGroupId: `com.${this.state.name.toLowerCase()}`
        };

        const emptyName = Promise.resolve()
            .then(() => {
                if (!newSpace.name || newSpace.name.trim() === "") {
                    this.addErrorMessage(AppFormer.translate("EmptyFieldValidation", ["Name"]));
                    return Promise.reject();
                } else {
                    return Promise.resolve();
                }
            });

        const duplicatedName = Promise.resolve()
            .then(() => this.props.organizationalUnitService.getOrganizationalUnit({name: newSpace.name}))
            .then(space => {
                if (space) {
                    this.addErrorMessage(AppFormer.translate("DuplicatedOrganizationalUnitValidation", ["Space"]));
                    return Promise.reject();
                } else {
                    return Promise.resolve();
                }
            });

        const validGroupId = Promise.resolve()
            .then(() => this.props.organizationalUnitService.isValidGroupId({proposedGroupId: newSpace.defaultGroupId}))
            .then(valid => {
                if (!valid) {
                    this.addErrorMessage(AppFormer.translate("InvalidSpaceName", []));
                    return Promise.reject();
                } else {
                    return Promise.resolve();
                }
            });

        this.setState({errorMessages: []}, () => Promise.resolve()
            .then(() => Promise.all([emptyName, duplicatedName, validGroupId]))
            .then(() => this.props.organizationalUnitService.createOrganizationalUnit0(newSpace))
            .then(i => this.props.onClose())
            .catch(() => this.setState(prevState => ({displayedErrorMessages: prevState.errorMessages}))));
    }

    private addErrorMessage(msg: string) {
        this.setState(prevState => ({errorMessages: [...prevState.errorMessages, msg]}));
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
                    {this.state.displayedErrorMessages.map(errorMessage =>
                        <div className={"alert alert-danger alert-dismissable"}>
                            <span className={"pficon pficon-error-circle-o"}/>
                            <span>{errorMessage}</span>
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