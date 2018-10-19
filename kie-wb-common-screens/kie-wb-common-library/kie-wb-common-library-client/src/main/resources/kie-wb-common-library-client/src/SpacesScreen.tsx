import * as React from "react";
import * as AppFormer from "appformer-js";
import {LibraryService} from "@kiegroup-ts-generated/kie-wb-common-library-api-rpc"
import {OrganizationalUnitService} from "@kiegroup-ts-generated/uberfire-structure-api-rpc"
import {OrganizationalUnit, OrganizationalUnitImpl} from "@kiegroup-ts-generated/uberfire-structure-api"
import {WorkspaceProjectContextChangeEvent} from "@kiegroup-ts-generated/uberfire-project-api";

interface Props {
    exposing: (self: () => SpacesScreenReactComponent) => void;
    organizationalUnitService: OrganizationalUnitService,
    libraryService: LibraryService,
}

interface State {
    spaces: Array<OrganizationalUnit>;
}

export class SpacesScreenReactComponent extends React.Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = {spaces: []};
        this.props.exposing(() => this);
    }

    private goToSpace(space: OrganizationalUnitImpl) {
        (window as any).appformerGwtBridge.sendEvent(AppFormer.marshall(new WorkspaceProjectContextChangeEvent({ou: space})));
        (window as any).goToLibrary();
    }

    private canCreateSpace() {
        //FIXME: fetch permissions from somewhere
        return true;
    }

    private newSpace() {
        //FIXME: Create popup with form and validations etc
        this.props.organizationalUnitService.createOrganizationalUnit0({
            name: `Tiago ${new Date().getTime()}`,
            owner: "admin",
            defaultGroupId: "org.uberfire.tiago"
        }).then(i => this.refreshSpaces());
    }

    public refreshSpaces() {
        this.props.libraryService.getOrganizationalUnits({}).then(spaces => {
            this.setState({spaces: spaces})
        });
    }

    componentDidMount() {
        this.refreshSpaces();
    }

    render() {
        if (this.state.spaces.length <= 0) {
            return <div className={"library"}>
                <div className={"col-sm-12 blank-slate-pf"}>
                    <div className={"blank-slate-pf-icon"}>
                        <span className={"pficon pficon pficon-add-circle-o"}/>
                    </div>
                    <h1>
                        Nothing here
                    </h1>
                    <p>
                        There are currently no Spaces available for you to view or edit. To get started, create a new Space
                    </p>
                    <div className={"blank-slate-pf-main-action"}>
                        <button className={"btn btn-primary btn-lg"} onClick={() => this.newSpace()}>
                            Add Space
                        </button>
                    </div>
                </div>
            </div>
        }

        return <>
        <div className={"library container-fluid"}>
            <div className={"row page-content-kie"}>
                <div className={"toolbar-pf"}>
                    <div className={"toolbar-pf-actions"}>
                        <div className={"toolbar-data-title-kie"}>
                            Spaces
                        </div>
                        <div className={"btn-group toolbar-btn-group-kie"}>
                            {this.canCreateSpace() &&
                            <button className={"btn btn-primary"} onClick={() => this.newSpace()}>
                                Add Space
                            </button>
                            }
                        </div>
                    </div>
                </div>
                <div className={"container-fluid container-cards-pf"}>
                    <div className={"row row-cards-pf"}>
                        {this.state.spaces.map(s =>
                            <Tile key={(s as OrganizationalUnitImpl).name}
                                  space={s as OrganizationalUnitImpl}
                                  onSelect={() => this.goToSpace(s as OrganizationalUnitImpl)}/>
                        )}
                    </div>
                </div>
            </div>
        </div>
        </>;
    }
}

function Tile(props: { space: OrganizationalUnitImpl, onSelect: () => void }) {
    return <>
    <div className={"col-xs-12 col-sm-6 col-md-4 col-lg-3"}>
        <div className={"card-pf card-pf-view card-pf-view-select card-pf-view-single-select"} onClick={() => props.onSelect()}>
            <div className={"card-pf-body"}>
                <div>
                    <h2 className={"card-pf-title"}> {props.space.name} </h2>
                    <h5> {props.space.contributors!.length} contributor(s) </h5>
                </div>
                <div className={"right"}>
                    <span className={"card-pf-icon-circle"}>
                        {props.space.repositories!.length}
                    </span>
                </div>
            </div>
        </div>
    </div>
    </>;
}