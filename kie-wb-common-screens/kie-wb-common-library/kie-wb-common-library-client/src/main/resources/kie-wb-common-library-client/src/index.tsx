import * as AppFormer from "appformer-js";
import * as React from "react";
import {SpacesScreen} from "./SpacesScreen";
import {LibraryService} from "@kiegroup-ts-generated/kie-wb-common-library-api-rpc"
import {AuthenticationService} from "@kiegroup-ts-generated/errai-security-server-rpc"
import {OrganizationalUnitService} from "@kiegroup-ts-generated/uberfire-structure-api-rpc"
import {PreferenceBeanServerStore} from "@kiegroup-ts-generated/uberfire-preferences-api-rpc";

export class SpacesScreenAppFormerComponent extends AppFormer.Screen {

    constructor() {
        super("LibraryOrganizationalUnitsScreen");
        this.af_isReact = true;
        this.af_componentTitle = "Spaces screen";
        this.af_subscriptions = {
            "org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent": e => this.self.refreshSpaces(),
            "org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent": e => this.self.refreshSpaces(),
            "org.kie.workbench.common.screens.library.api.sync.ClusterLibraryEvent": e => this.self.refreshSpaces(),
        };
        this.af_componentService = {};
    }

    private self: SpacesScreen;

    af_onOpen(): void {
        this.self.refreshSpaces();
    }

    af_componentRoot(): AppFormer.Element {
        return <SpacesScreen exposing={ref => this.self = ref()}
                             libraryService={new LibraryService()}
                             authenticationService={new AuthenticationService()}
                             organizationalUnitService={new OrganizationalUnitService()}
                             preferenceBeanServerStore={new PreferenceBeanServerStore()}/>;

    }

}

AppFormer.register(new SpacesScreenAppFormerComponent());