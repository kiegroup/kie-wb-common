import * as AppFormer from "appformer-js";
import * as React from "react";
import {SpacesScreenReactComponent} from "./SpacesScreen";
import {LibraryService} from "@kiegroup-ts-generated/kie-wb-common-library-api-rpc"
import {OrganizationalUnitService} from "@kiegroup-ts-generated/uberfire-structure-api-rpc"

export class SpacesScreen extends AppFormer.Screen {

    constructor() {
        super();
        this.isReact = true;
        this.af_componentTitle = "Spaces screen";
        this.af_componentId = "LibraryOrganizationalUnitsScreen";
        this.af_subscriptions = {
            "org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent": e => this.self.refreshSpaces(),
            "org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent": e => this.self.refreshSpaces(),
            "org.kie.workbench.common.screens.library.api.sync.ClusterLibraryEvent": e => this.self.refreshSpaces(),
        };
        this.af_componentService = {};
    }

    private self: SpacesScreenReactComponent;

    af_onOpen(): void {
        this.self.refreshSpaces();
    }

    af_componentRoot(root?: { ss: AppFormer.Screen[]; ps: AppFormer.Perspective[] }): AppFormer.Element {
        return <SpacesScreenReactComponent exposing={c => this.self = c()}
                                           organizationalUnitService={new OrganizationalUnitService()}
                                           libraryService={new LibraryService()}/>;
    }

}

AppFormer.register({SpacesScreen});