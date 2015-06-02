package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.uberfire.commons.data.Pair;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class ConfigurationComboBoxItemWidget extends Composite {

    interface ConfigurationItemWidgetBinder extends UiBinder<Widget, ConfigurationComboBoxItemWidget> {

    }

    private static ConfigurationItemWidgetBinder uiBinder = GWT.create( ConfigurationItemWidgetBinder.class );

    @UiField
    PropertyEditorComboBox extensionItem;

    @UiField
    PropertyEditorItemLabel extensionItemLabel;

    private String widgetId;

    @PostConstruct
    private void setup() {
        super.initWidget( uiBinder.createAndBindUi( this ) );
    }

    public PropertyEditorComboBox getExtensionItem() {
        return this.extensionItem;
    }

    public PropertyEditorItemLabel getExtensionItemLabel() {
        return this.extensionItemLabel;
    }

    public Pair<String, String> getSelectedItem() {
        return extensionItem.getSelectedPair( extensionItem.getSelectedIndex() );
    }

    public void setSelectedItem( final String text ) {
        extensionItem.setSelectItemByText( text );
    }

    public void initExtensionItem( final List<Pair<String, String>> items ) {
        for ( Pair<String, String> p : items ) {
            extensionItem.addItem( p );
        }
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId( final String widgetId ) {
        this.widgetId = widgetId;
    }

    public void clear() {
        extensionItem.clear();
    }
}