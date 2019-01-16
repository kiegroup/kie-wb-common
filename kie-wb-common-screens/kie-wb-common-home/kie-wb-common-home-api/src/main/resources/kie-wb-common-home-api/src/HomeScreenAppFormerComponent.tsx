import * as AppFormer from "appformer-js";
import * as React from "react";
import {HomeScreenView} from "./view/HomeScreenView";
import {HomeScreenProvider} from "./model/HomeScreenProvider";

export class HomeScreenAppFormerComponent extends AppFormer.Screen {

    private readonly provider: HomeScreenProvider;

    public constructor(modelProvider: HomeScreenProvider) {
        super("org.kie.workbench.common.screens.home.client.HomePresenter");
        this.af_isReact = true;
        this.af_componentTitle = AppFormer.translate("homeName", []);

        this.provider = modelProvider;
    }

    public af_componentRoot(children?: any): AppFormer.Element {
        return <HomeScreenView contentProvider={this.provider}/>;
    }
}