package org.kie.workbench.common.services.shared.project;

import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class KieModulePackages {

    private Set<Package> packages;

    private Package defaultPackage;

    public KieModulePackages() { }

    public KieModulePackages(Set<Package> packages, Package defaultPackage) {
        this.packages = packages;
        this.defaultPackage = defaultPackage;
    }

    public Set<Package> getPackages() {
        return packages;
    }

    public void setPackages(Set<Package> packages) {
        this.packages = packages;
    }

    public Package getDefaultPackage() {
        return defaultPackage;
    }

    public void setDefaultPackage(Package defaultPackage) {
        this.defaultPackage = defaultPackage;
    }
}
