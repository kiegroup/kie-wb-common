package org.kie.workbench.common.widgets.client.docks;

import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceHolderBaseViewImpl
        extends SimplePanel
        implements PlaceHolderBaseView {

    private PlaceHolderBase presenter;

    @Override
    public PlaceHolderBase getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(PlaceHolderBase presenter) {
        this.presenter = presenter;
    }
}
