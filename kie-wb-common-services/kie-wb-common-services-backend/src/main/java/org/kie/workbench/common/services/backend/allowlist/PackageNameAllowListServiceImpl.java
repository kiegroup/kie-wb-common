/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.allowlist;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.allowlist.PackageNameAllowListService;
import org.kie.workbench.common.services.shared.allowlist.AllowList;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

/**
 * Represents a "allow list" of permitted package names for use with authoring
 */
@Service
@ApplicationScoped
public class PackageNameAllowListServiceImpl
        implements PackageNameAllowListService {

    private IOService ioService;
    private KieModuleService moduleService;
    private PackageNameAllowListLoader loader;
    private PackageNameAllowListSaver saver;

    public PackageNameAllowListServiceImpl() {
    }

    @Inject
    public PackageNameAllowListServiceImpl(final @Named("ioStrategy") IOService ioService,
                                           final KieModuleService moduleService,
                                           final PackageNameAllowListLoader loader,
                                           final PackageNameAllowListSaver saver) {
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.loader = loader;
        this.saver = saver;
    }

    public void createModuleAllowList(final Path packageNamesAllowListPath,
                                      final String initialContent) {
        if (ioService.exists(Paths.convert(packageNamesAllowListPath))) {
            throw new FileAlreadyExistsException(packageNamesAllowListPath.toString());
        } else {
            ioService.write(Paths.convert(packageNamesAllowListPath),
                                                           initialContent);
        }
    }

    /**
     * Filter the provided Package names by the Module's allow list
     * @param module Module for which to filter Package names
     * @param packageNames All Package names in the Module
     * @return A filtered collection of Package names
     */
    @Override
    public AllowList filterPackageNames(final Module module,
                                        final Collection<String> packageNames) {
        if (packageNames == null) {
            return new AllowList();
        } else if (module instanceof KieModule) {

            final AllowList allowList = load(((KieModule) module).getPackageNamesAllowListPath());

            if (allowList.isEmpty()) {
                return new AllowList(packageNames);
            } else {
                for (Package aPackage : moduleService.resolvePackages(module)) {
                    allowList.add(aPackage.getPackageName());
                }

                return new PackageNameAllowListFilter(packageNames,
                                                      allowList).getFilteredPackageNames();
            }
        } else {
            return new AllowList(packageNames);
        }
    }

    @Override
    public AllowList load(final Path packageNamesAllowListPath) {
        return loader.load(packageNamesAllowListPath);
    }

    @Override
    public Path save(final Path path,
                     final AllowList content,
                     final Metadata metadata,
                     final String comment) {
        return saver.save(path,
                          content,
                          metadata,
                          comment);
    }
}

