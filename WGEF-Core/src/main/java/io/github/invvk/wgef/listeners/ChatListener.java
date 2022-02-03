package io.github.invvk.wgef.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final WGEFPlugin plugin;

    public ChatListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ApplicableRegionSet regions = this.plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());

        String prefix = WGEFUtils.queryValueUnchecked(player, player.getWorld(), regions.getRegions(), WGEFlags.CHAT_PREFIX);
        String suffix = WGEFUtils.queryValueUnchecked(player, player.getWorld(), regions.getRegions(), WGEFlags.CHAT_SUFFIX);

        if (prefix != null)
            event.setFormat(prefix + " " + event.getFormat());


        if (suffix != null)
            event.setFormat(event.getFormat() + " " + suffix);

    }

}
