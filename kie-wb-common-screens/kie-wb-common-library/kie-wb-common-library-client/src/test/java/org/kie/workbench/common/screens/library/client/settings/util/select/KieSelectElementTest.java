package org.kie.workbench.common.screens.library.client.settings.util.select;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.Node;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieSelectElement.Option;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class KieSelectElementTest {

    @Mock
    private KieSelectElement.View view;

    @Mock
    private KieSelectElement.OptionsListPresenter optionsListPresenter;

    private KieSelectElement kieSelectElement;

    @Before
    public void before() {
        kieSelectElement = Mockito.spy(new KieSelectElement(view, optionsListPresenter, new Elemental2DomUtil()));
    }

    @Test
    public void testSetup() {
        final HTMLElement viewRoot = Mockito.spy(new HTMLElement());
        viewRoot.innerHTML = "bar";
        Mockito.doReturn(viewRoot).when(view).getElement();

        final HTMLSelectElement selectElement = Mockito.spy(new HTMLSelectElement());
        Mockito.doReturn(selectElement).when(view).getSelect();

        final Element container = Mockito.spy(new Element() {
            @Override
            public Node appendChild(final Node node) {
                if (node instanceof HTMLElement) {
                    this.innerHTML += ((HTMLElement) node).innerHTML;
                }
                return node;
            }
        });

        container.innerHTML = "";

        final List<Option> options =
                singletonList(new Option("Label", "Value"));

        kieSelectElement.setup(
                container,
                options,
                "Value",
                value -> {
                });

        Mockito.verify(view).setValue(Matchers.eq("Value"));
        Mockito.verify(view).initSelect();
        Mockito.verify(optionsListPresenter).setup(Matchers.eq(selectElement), Matchers.eq(options), Matchers.any());
        assertEquals("bar", container.innerHTML);
    }

    @Test
    public void testOnChange() {
        final AtomicInteger i = new AtomicInteger(0);
        Mockito.doReturn("Test").when(kieSelectElement).getValue();

        kieSelectElement.onChange = value -> {
            assertEquals("Test", value);
            i.incrementAndGet();
        };

        kieSelectElement.onChange();

        assertEquals(1, i.get());
    }
}