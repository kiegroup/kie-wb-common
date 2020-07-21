package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import org.junit.Assert;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.uberfire.java.nio.file.Path;

import org.junit.Test;
import org.mockito.Mockito;
import org.uberfire.io.IOService;

public class JavaFileIndexerTest {

    @Test
    public void testFillIndexBuilderWithUnavailablePath () throws Exception {
        final IOService ioService = Mockito.mock(IOService.class);
        final JavaFileIndexer indexer = Mockito.mock(JavaFileIndexer.class);
        final Path mockPath = Mockito.mock(Path.class);
        Mockito.when(ioService.exists(mockPath)).thenReturn(false);
        final IndexBuilder builder = indexer.fillIndexBuilder(mockPath);
        Assert.assertNull(builder);
    }
}
