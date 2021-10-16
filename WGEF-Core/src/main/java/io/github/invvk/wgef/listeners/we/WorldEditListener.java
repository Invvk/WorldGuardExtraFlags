package io.github.invvk.wgef.listeners.we;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import io.github.invvk.wgef.v7.worldedit.WGEFExtentHandler;

public class WorldEditListener {

    @Subscribe(priority = EventHandler.Priority.NORMAL)
    public void onEditSessionEvent(EditSessionEvent event) {
        Actor actor = event.getActor();
        if (actor != null && actor.isPlayer()) {
            event.setExtent(new WGEFExtentHandler(event.getWorld(), event.getExtent(), (Player) actor));
        }
    }

}
