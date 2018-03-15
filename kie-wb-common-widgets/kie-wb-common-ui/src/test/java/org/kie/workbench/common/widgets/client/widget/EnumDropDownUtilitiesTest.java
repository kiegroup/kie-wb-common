package org.kie.workbench.common.widgets.client.widget;

import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class EnumDropDownUtilitiesTest {

    @Mock
    private ListBox listBox;

    @Mock
    private DropDownData dropDownData;

    @Mock
    private Path path;

    private String[] enums;

    private EnumDropDownUtilities utilities = new EnumDropDownUtilities();

    @Before
    public void setUpEnums() throws Exception {
        enums = new String[]{"\"John, junior\"", "\"John, senior\"", "John", "Mark"};
        when(dropDownData.getFixedList()).thenReturn(enums);
    }

    @Test
    public void testFillDropDownData_NoValue() throws Exception {
        // in the form org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown#encodeSelectedItems()
        final String cellValue = "";
        final boolean isMultiSelect = false;
        utilities.setDropDownData(cellValue, dropDownData, isMultiSelect, path, listBox);

        Stream.of(enums).forEach(enumValue -> verify(listBox).addItem(enumValue));
        verify(listBox, never()).setItemSelected(anyInt(), anyBoolean());
    }

    @Test
    public void testFillDropDownData_StandardValue() throws Exception {
        // in the form org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown#encodeSelectedItems()
        final String cellValue = "John";
        final boolean isMultiSelect = false;
        utilities.setDropDownData(cellValue, dropDownData, isMultiSelect, path, listBox);

        Stream.of(enums).forEach(enumValue -> verify(listBox).addItem(enumValue));
        final int johnIndex = 2;
        verify(listBox).setItemSelected(johnIndex, true);
    }

    @Test
    public void testFillDropDownData_ValueInCommaInside() throws Exception {
        // in the form org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown#encodeSelectedItems()
        final String cellValue = "\"John, senior\"";
        final boolean isMultiSelect = false;
        utilities.setDropDownData(cellValue, dropDownData, isMultiSelect, path, listBox);

        Stream.of(enums).forEach(enumValue -> verify(listBox).addItem(enumValue));
        final int johnSeniorIndex = 1;
        verify(listBox).setItemSelected(johnSeniorIndex, true);
    }

    @Test
    public void testFillDropDownData_MultiSelect_NoValue() throws Exception {
        // in the form org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown#encodeSelectedItems()
        final String cellValue = "(  )";
        final boolean isMultiSelect = true;
        utilities.setDropDownData(cellValue, dropDownData, isMultiSelect, path, listBox);

        Stream.of(enums).forEach(enumValue -> verify(listBox).addItem(enumValue));
        verify(listBox, never()).setItemSelected(anyInt(), anyBoolean());
    }

    @Test
    public void testFillDropDownData_MultiSelect_StandardValues() throws Exception {
        // in the form org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown#encodeSelectedItems()
        final String cellValue = "( \"John\",\"Mark\" )";
        final boolean isMultiSelect = true;
        utilities.setDropDownData(cellValue, dropDownData, isMultiSelect, path, listBox);

        Stream.of(enums).forEach(enumValue -> verify(listBox).addItem(enumValue));
        final int johnIndex = 2;
        final int markIndex = 3;
        verify(listBox).setItemSelected(johnIndex, true);
        verify(listBox).setItemSelected(markIndex, true);
    }

    @Test
    public void testFillDropDownData_MultiSelect_ValueInCommaInside() throws Exception {
        // in the form org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown#encodeSelectedItems()
        final String cellValue = "( \"John\",\"\"John, junior\"\" )";
        final boolean isMultiSelect = true;
        utilities.setDropDownData(cellValue, dropDownData, isMultiSelect, path, listBox);

        Stream.of(enums).forEach(enumValue -> verify(listBox).addItem(enumValue));
        final int johnJuniorIndex = 0;
        final int johnIndex = 2;
        verify(listBox).setItemSelected(johnIndex, true);
        verify(listBox).setItemSelected(johnJuniorIndex, true);
    }
}
