package io.github.invvk.wgef;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import io.github.invvk.wgef.abstraction.IManager;
import io.github.invvk.wgef.abstraction.IWGFork;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.dependencies.IEssentialsDependency;
import io.github.invvk.wgef.abstraction.dependencies.IFAWEDependency;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.flags.handler.command.CommandOnEntryHandler;
import io.github.invvk.wgef.abstraction.flags.handler.command.CommandOnExitHandler;
import io.github.invvk.wgef.abstraction.flags.handler.command.ConsoleCommandOnEntryHandler;
import io.github.invvk.wgef.abstraction.flags.handler.command.ConsoleCommandOnExitHandler;
import io.github.invvk.wgef.abstraction.flags.handler.essentials.GodModeHandler;
import io.github.invvk.wgef.abstraction.flags.handler.player.*;
import io.github.invvk.wgef.abstraction.flags.handler.teleport.TeleportEntryHandler;
import io.github.invvk.wgef.abstraction.flags.handler.teleport.TeleportExitHandler;
import io.github.invvk.wgef.abstraction.wrapper.AbstractSessionManagerWrapper;
import io.github.invvk.wgef.dependency.EssentialsDependency;
import io.github.invvk.wgef.dependency.FAWEDependency;
import io.github.invvk.wgef.listeners.*;
import io.github.invvk.wgef.listeners.essentials.GodModeListener;
import io.github.invvk.wgef.listeners.we.WorldEditListener;
import io.github.invvk.wgef.updater.UpdateChecker;
import io.github.invvk.wgef.v7.IWG7Fork;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class WGPluginManager implements IManager {

    private IEssentialsDependency essentials;
    private IFAWEDependency fawe;

    private IWGFork fork;

    private final WGEFPlugin plugin;

    public WGPluginManager(WGEFPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        this.fork = new IWG7Fork();
        WGEFUtils.setFork(this.fork);

        FlagRegistry registry = this.fork.getFlagReg();
        registry.register(WGEFlags.COMMAND_ON_ENTRY);
        registry.register(WGEFlags.COMMAND_ON_EXIT);
        registry.register(WGEFlags.CONSOLE_COMMAND_ON_ENTRY);
        registry.register(WGEFlags.CONSOLE_COMMAND_ON_EXIT);
        registry.register(WGEFlags.TELEPORT_ON_ENTRY);
        registry.register(WGEFlags.TELEPORT_ON_EXIT);
        registry.register(WGEFlags.GIVE_EFFECTS);
        registry.register(WGEFlags.BLOCKED_EFFECTS);
        registry.register(WGEFlags.ALLOW_BLOCK_PLACE);
        registry.register(WGEFlags.ALLOW_BLOCK_BREAK);
        registry.register(WGEFlags.DENY_BLOCK_PLACE);
        registry.register(WGEFlags.DENY_BLOCK_BREAK);
        registry.register(WGEFlags.ALLOWED_BLOCK_DROPS);
        registry.register(WGEFlags.BLOCKED_BLOCK_DROPS);
        registry.register(WGEFlags.WORLD_EDIT);
        registry.register(WGEFlags.WALK_SPEED);
        registry.register(WGEFlags.GLIDE);
        registry.register(WGEFlags.FLY);
        registry.register(WGEFlags.FROST_WALKER);
        registry.register(WGEFlags.CHAT_PREFIX);
        registry.register(WGEFlags.CHAT_SUFFIX);
        registry.register(WGEFlags.KEEP_EXP);
        registry.register(WGEFlags.KEEP_INVENTORY);
        registry.register(WGEFlags.FLY_SPEED);
        registry.register(WGEFlags.NETHER_PORTALS);
        registry.register(WGEFlags.ITEM_DURABILITY);
        registry.register(WGEFlags.VILLAGER_TRADE);
        registry.register(WGEFlags.ALLOW_ENTITY_PLACE);
        registry.register(WGEFlags.DENY_ENTITY_PLACE);

        this.dependency();
    }

    @Override
    public void dependency() {
        this.essentials = EssentialsDependency.load(this);

        if (WGEFUtils.isFAWEPresent())
            this.fawe = FAWEDependency.load(this.plugin);
    }

    @Override
    public void enable() {
        this.fork.enable(this.plugin);
        AbstractSessionManagerWrapper sessionManager = this.getFork().getSessionManager();
        sessionManager.registerHandler(CommandOnEntryHandler.FACTORY);
        sessionManager.registerHandler(CommandOnExitHandler.FACTORY);
        sessionManager.registerHandler(ConsoleCommandOnEntryHandler.FACTORY);
        sessionManager.registerHandler(ConsoleCommandOnExitHandler.FACTORY);
        sessionManager.registerHandler(TeleportEntryHandler.FACTORY(plugin));
        sessionManager.registerHandler(TeleportExitHandler.FACTORY(plugin));
        sessionManager.registerHandler(BlockEffectFlagHandler.FACTORY);
        sessionManager.registerHandler(GiveEffectFlagHandler.FACTORY);
        sessionManager.registerHandler(GlideFlagHandler.FACTORY);
        sessionManager.registerHandler(FlyFlagHandler.FACTORY);
        sessionManager.registerHandler(WalkSpeedFlagHandler.FACTORY);
        sessionManager.registerHandler(FlySpeedFlagHandler.FACTORY);

        this.getEssentials().ifPresent(e -> {
            registerEvents(new GodModeListener());
            sessionManager.registerHandler(GodModeHandler.FACTORY(this));
        });

        WorldEdit.getInstance().getEventBus().register(new WorldEditListener());

        this.registerEvents(new BlockedPotionEffectListener(this.plugin),
                new BlockListener(this.plugin),
                new GlideListener(this.plugin),
                new FlyListener(this.plugin),
                new FrostWalkerListener(this.plugin),
                new ChatListener(this.plugin),
                new DeathListener(this.plugin),
                new NetherPortalListener(this.plugin),
                new ItemListener(this.plugin),
                new SpeedListener(this.plugin),
                new VillagerTradeListener(this.plugin),
                new EntityPlaceListener(this.plugin));

        this.updateChecker();
        this.metrics();
    }

    @Override
    public void disable() {
        if (Bukkit.getOnlinePlayers().size() == 0)
            return;

        Bukkit.getOnlinePlayers().forEach(player -> {
            WalkSpeedFlagHandler walk = plugin.getFork().getSessionManager().get(player)
                    .getHandler(WalkSpeedFlagHandler.class);
            FlySpeedFlagHandler fly = plugin.getFork().getSessionManager().get(player)
                    .getHandler(FlySpeedFlagHandler.class);

            if (walk != null && walk.getOriginalSpeed() != null)
                player.setWalkSpeed(walk.getOriginalSpeed());

            if (fly != null && fly.getOriginalSpeed() != null)
                player.setFlySpeed(fly.getOriginalSpeed());
        });
    }

    @Override
    public IWGFork getFork() {
        return this.fork;
    }

    @Override
    public Optional<IEssentialsDependency> getEssentials() {
        return Optional.ofNullable(essentials);
    }

    @Override
    public Optional<IFAWEDependency> getFAWE() {
        return Optional.ofNullable(fawe);
    }

    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, this.plugin);
    }

    private void updateChecker() {
        if (!plugin.getConfig().getBoolean("update-checker"))
            return;

        new UpdateChecker(this.plugin, 96894)
                .getVersion(version -> {
                    if (plugin.getDescription().getVersion().equals(version)) {
                        plugin.getLogger().info("You are running the latest build!");
                        return;
                    }
                    plugin.getLogger().info("New update found: " + version);
                    plugin.getLogger().info("download it at: https://www.spigotmc.org/resources/96894/");
                });
    }

    private void metrics() {
        Metrics metrics = new Metrics(this.plugin, 13119);

        metrics.addCustomChart(new AdvancedPie("flag_usage", () -> {
            final Set<Flag<?>> flags = WGEFlags.values();
            Map<Flag<?>, Integer> valueMap = flags.stream().collect(Collectors.toMap((value) -> value, (v) -> 0));
            WGEFUtils.getFork().getRegionContainer().getLoaded()
                    .forEach(regionManager -> {
                        regionManager.getRegions().values().forEach(region -> {
                            region.getFlags().keySet().forEach(flag -> {
                                valueMap.computeIfPresent(flag, (key, value) -> value + 1);
                            });
                        });
                    });
            return valueMap.entrySet().stream().collect(Collectors.toMap((v) -> v.getKey().getName(), Map.Entry::getValue));
        }));
    }

}
