import * as AppFormer from "appformer-js";
import * as React from "react";

export class HomePerspectiveAppFormerComponent extends AppFormer.Perspective {
    constructor() {
        super("HomePerspective");
        this.af_isReact = true;
        this.af_isDefault = true;
        this.af_name = AppFormer.translate("homePerspectiveName", []);
    }

    public af_componentRoot(children?: any): AppFormer.Element {
        return (
            <div af-js-component={"org.kie.workbench.common.screens.home.client.HomePresenter"} />
        );
    }
}

AppFormer.register(new HomePerspectiveAppFormerComponent());