package io.github.invvk.wgef;

import io.github.invvk.wgef.abstraction.IWGEFPlugin;
import io.github.invvk.wgef.abstraction.IWGFork;
import kr.entree.spigradle.annotations.SpigotPlugin;

import java.io.File;

@SpigotPlugin
public class WGEFPlugin extends IWGEFPlugin {

    private static WGEFPlugin inst;

    private WGPluginManager manager;

    @Override
    public void onLoad() {
        final File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists())
            saveResource("config.yml", false);
        this.manager = new WGPluginManager(this);
        this.manager.load();
        super.onLoad();
    }

    @Override
    public void onEnable() {
        inst = this;
        this.manager.enable();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        inst = null;
        this.manager.disable();
        super.onDisable();
    }

    public static WGEFPlugin inst() {
        return inst;
    }

    @Override
    public IWGFork getFork() {
        return this.manager.getFork();
    }
}
