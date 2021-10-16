package io.github.invvk.wgef.abstraction.flags.handler.player;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.flags.data.PotionEffectDetails;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockEffectFlagHandler extends FlagValueChangeHandler<Set<PotionEffectType>> {

    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<BlockEffectFlagHandler> {
        @Override
        public BlockEffectFlagHandler create(Session session) {
            return new BlockEffectFlagHandler(session);
        }

    }
    private Set<PotionEffectType> blocked;
    private final Map<PotionEffectType, PotionEffectDetails> effects_on_leave = new HashMap<>();

    protected BlockEffectFlagHandler(Session session) {
        super(session, WGEFlags.BLOCKED_EFFECTS);
        this.blocked = new HashSet<>();
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet set, Set<PotionEffectType> value) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        this.blocked = WGEFUtils.queryValue(player, player.getWorld(), set.getRegions(), WGEFlags.BLOCKED_EFFECTS);
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<PotionEffectType> currentValue, Set<PotionEffectType> lastValue, MoveType moveType) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);

        this.blocked = WGEFUtils.queryValue(player, player.getWorld(), toSet.getRegions(), WGEFlags.BLOCKED_EFFECTS);

        if (blocked != null && !blocked.isEmpty()) {
            if (!player.getActivePotionEffects().isEmpty()) {
                player.getActivePotionEffects().forEach(effect -> {
                    PotionEffectType type = effect.getType();
                    if (!blocked.contains(type))
                        return;

                    this.effects_on_leave.put(type, new PotionEffectDetails(effect.getDuration(),
                            effect.getAmplifier(),
                            effect.isAmbient(),
                            effect.hasParticles()));

                    player.removePotionEffect(type);
                });
            }
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<PotionEffectType> lastValue, MoveType moveType) {
        Player player = WGEFUtils.wrapPlayer(localPlayer);
        if (!this.effects_on_leave.isEmpty())
            this.effects_on_leave.forEach((type, details) ->
                    player.addPotionEffect(new PotionEffect(type, details.getTimeLeftInTicks(),
                            details.getAmplifier(),
                            details.isAmbient(),
                            details.isParticles())));
        this.effects_on_leave.clear();
        this.blocked = null;
        return true;
    }

    public Set<PotionEffectType> getBlockedPotions() {
        return blocked;
    }
    
}
