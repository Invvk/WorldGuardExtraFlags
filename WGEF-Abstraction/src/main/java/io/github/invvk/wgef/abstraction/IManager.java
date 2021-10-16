package io.github.invvk.wgef.abstraction;

import io.github.invvk.wgef.abstraction.dependencies.IEssentialsDependency;
import io.github.invvk.wgef.abstraction.dependencies.IFAWEDependency;

import java.util.Optional;

public interface IManager {

    void load();
    void dependency();
    void enable();

    void disable();

    IWGFork getFork();

    Optional<IEssentialsDependency> getEssentials();
    Optional<IFAWEDependency> getFAWE();

}
