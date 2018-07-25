package org.kie.workbench.common.screens.library.client.settings.util.sections;

import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;


public abstract class SectionListItemPresenter<T, P, V extends ListItemView<? extends ListItemPresenter<T, P, V>>> extends ListItemPresenter<T, P, V> {

    public SectionListItemPresenter(V view) {
        super(view);
    }

    public SectionListPresenter<T , ?> getSectionListPresenter() {
        return (SectionListPresenter<T , ?>)this.getListPresenter();
    }
    
}
