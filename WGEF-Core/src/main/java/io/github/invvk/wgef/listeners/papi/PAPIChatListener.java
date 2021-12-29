package io.github.invvk.wgef.listeners.papi;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.github.invvk.wgef.WGEFPlugin;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PAPIChatListener implements Listener {

    private final WGEFPlugin plugin;

    public PAPIChatListener(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ApplicableRegionSet regions = this.plugin.getFork().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());

        String prefix = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.CHAT_PREFIX);
        String suffix = WGEFUtils.queryValue(player, player.getWorld(), regions.getRegions(), WGEFlags.CHAT_SUFFIX);

        if (prefix != null)
            event.setFormat(PlaceholderAPI.setPlaceholders(player, prefix) + " " + event.getFormat());


        if (suffix != null)
            event.setFormat(event.getFormat() + " " + PlaceholderAPI.setPlaceholders(player, suffix));

    }

}
