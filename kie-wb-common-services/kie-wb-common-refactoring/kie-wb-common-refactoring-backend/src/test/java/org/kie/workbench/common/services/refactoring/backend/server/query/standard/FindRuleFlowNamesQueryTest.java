package org.kie.workbench.common.services.refactoring.backend.server.query.standard;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static java.lang.String.format;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FindRuleFlowNamesQueryTest {

    @Mock
    private KObject kObject;
    @Mock
    private KProperty property;
    @Mock
    private IOService ioService;
    private static final String GROUP_NAME = "Group_Name";
    private static final String FILE_NOT_EXIST_PATH = "default://master@SpaceA/ProjectA/src/main/resources/Rule_not_exist.rdrl";
    private static final String FILE_NAME = "Rule_number_two.rdrl";
    private static final String FILE_PATH = "default://master@SpaceA/ProjectA/src/main/resources/" + FILE_NAME;
    private static final URI FILE_NOT_EXIST_URI = URI.create(FILE_NOT_EXIST_PATH);
    private static final URI FILE_URI = URI.create(FILE_PATH);
    private static final SimpleFileSystemProvider fileSystemProvider = new SimpleFileSystemProvider();
    private static final Path path = fileSystemProvider.getPath(FILE_URI);
    private List<KObject> kObjects = new ArrayList<>();
    private List<KProperty<?>> properties = new ArrayList<>();

    // Tested classes
    private static final FindRuleFlowNamesQuery query = new FindRuleFlowNamesQuery();
    private ResponseBuilder testedBuilder;

    @Before
    public void init() {
        // IO Service mock
        when(ioService.get(FILE_NOT_EXIST_URI)).thenThrow(new FileSystemNotFoundException(format("No filesystem for uri %s found.", FILE_NOT_EXIST_URI.toString())));
        when(ioService.get(FILE_URI)).thenReturn(path);
        query.setIoService(ioService);

        // Indexed RuleFlow groups mock
        when(property.getName()).thenReturn(FindRuleFlowNamesQuery.SHARED_TERM);
        when(property.getValue()).thenReturn(GROUP_NAME);
        properties.add(property);

        // Class under test
        testedBuilder = query.getResponseBuilder();
    }

    @Test
    public void testNullObject() {
        fail("finish it");
    }

    @Test
    public void testNoProperties() {
        fail("finish it");
    }

    @Test
    public void testNonGroupTermIsIgnored() {
        fail("finish it");
    }

    @Test
    public void testNewGroupAdded() {
        fail("finish it");
    }

    @Test
    public void testExistentGroupUpdated() {
        fail("finish it");
    }

    @Test
    public void testFileWithGroupIsDeletedOrNotExist() {
        when(kObject.getProperties()).thenReturn(properties);
        when(kObject.getKey()).thenReturn(FILE_NOT_EXIST_PATH);
        kObjects.add(kObject);
        testedBuilder.buildResponse(kObjects);
        fail("finish it");
    }
}
