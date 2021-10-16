package io.github.invvk.wgef.v7.wrapper;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.invvk.wgef.abstraction.wrapper.AbstractSessionManagerWrapper;
import org.bukkit.entity.Player;

public class SessionManagerWrapper extends AbstractSessionManagerWrapper {

    public SessionManagerWrapper(SessionManager manager) {
        super(manager);
    }

    @Override
    public Session get(Player player) {
        return this.sessionManager.get(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    @Override
    public Session getIfPresent(Player player) {
        return this.sessionManager.getIfPresent(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    @Override
    public void registerHandler(Handler.Factory<? extends Handler> factory) {
        this.sessionManager.registerHandler(factory, null);
    }

}
