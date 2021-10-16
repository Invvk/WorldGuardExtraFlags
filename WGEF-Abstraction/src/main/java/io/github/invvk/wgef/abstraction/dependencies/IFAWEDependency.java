package io.github.invvk.wgef.abstraction.dependencies;

public interface IFAWEDependency extends DependencyStatus {

    void injectOption();
    void cancelIntersectingExtents();
}
