package org.kie.workbench.common.screens.explorer.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ExplorerMenuViewImplTest {

    private ExplorerMenuViewImpl view;

    @Before
    public void setup() {
        view = new ExplorerMenuViewImpl() {

            @Override
            protected String getClientId() {
                return "123";
            }

            @Override
            protected AnchorListItem getWidgets( final String text ) {
                return mock( AnchorListItem.class );
            }
        };
    }

    @Test
    public void testGetDownloadUrl() throws Exception {
        assertEquals( "archive?clientId=123&attachmentPath=", view.getDownloadUrl( path() ) );
    }

    private Path path() {
        return new Path() {
            @Override
            public String getFileName() {
                return "";
            }

            @Override
            public String toURI() {
                return "";
            }

            @Override
            public int compareTo( final Path o ) {
                return 0;
            }
        };
    }
}
