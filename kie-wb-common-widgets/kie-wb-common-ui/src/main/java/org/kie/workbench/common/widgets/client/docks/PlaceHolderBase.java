package org.kie.workbench.common.widgets.client.docks;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

public class PlaceHolderBase {

    private PlaceHolderBaseView view;

    public PlaceHolderBase() {
        // CDI
    }

    @Inject
    public void DockPlaceHolder(final PlaceHolderBaseView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "DockPlaceHolder"; // Never used.
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public void setView(final IsWidget widget) {
        view.clear();
        view.setWidget(widget);
    }
}
