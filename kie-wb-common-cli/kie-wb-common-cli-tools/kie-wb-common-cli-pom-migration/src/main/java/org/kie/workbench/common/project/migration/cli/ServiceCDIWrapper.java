package org.kie.workbench.common.project.migration.cli;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class ServiceCDIWrapper {

    private IOService ioService;

    private CommentedOptionFactory commentedOptionFactory;

    private Repository systemRepository;

    private IOService systemIoService;

    @Inject
    public ServiceCDIWrapper(final @Named("ioStrategy") IOService ioService,
                                       final CommentedOptionFactory commentedOptionFactory,
                                       final @Named("system") Repository systemRepository,
                                       final @Named("configIO") IOService systemIoService) {
        this.ioService = ioService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.systemRepository = systemRepository;
        this.systemIoService = systemIoService;
    }

    /*public IOService getIOService() {
        return ioService;
    }*/

    public void write(Path path, String content, String comment) {
        ioService.write(Paths.convert(path), content, commentedOptionFactory.makeCommentedOption(comment));
    }

    public Repository getSystemRepository() {
        return systemRepository;
    }

    public IOService getSystemIoService() {
        return systemIoService;
    }
}
