package org.kie.workbench.common.widgets.client.docks;

import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class DockPlaceHolderPlace
        extends DefaultPlaceRequest {

    private static final String DEFAULT_IDENTIFIER = "org.docks.PlaceHolder";

    public DockPlaceHolderPlace(final String identifier,
                                final String name) {
        super(identifier);
        addParameter("name", name);
    }

    public DockPlaceHolderPlace(final String name) {
        this(DEFAULT_IDENTIFIER,
             name);
    }
}
