package io.github.invvk.wgef.abstraction.flags.handler.player;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.invvk.wgef.abstraction.WGEFUtils;
import io.github.invvk.wgef.abstraction.flags.WGEFlags;
import io.github.invvk.wgef.abstraction.flags.data.PotionEffectDetails;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GiveEffectFlagHandler extends FlagValueChangeHandler<Set<PotionEffect>> {

    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<GiveEffectFlagHandler> {
        @Override
        public GiveEffectFlagHandler create(Session session) {
            return new GiveEffectFlagHandler(session);
        }
    }

    protected GiveEffectFlagHandler(Session session) {
        super(session, WGEFlags.GIVE_EFFECTS);
    }

    private final Map<PotionEffectType, PotionEffectDetails> map = new HashMap<>();
    private Set<PotionEffect> give_potions;
    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet set, Set<PotionEffect> value) {}

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<PotionEffect> currentValue, Set<PotionEffect> lastValue, MoveType moveType) {
        final Player player = WGEFUtils.wrapPlayer(localPlayer);
        this.give_potions = WGEFUtils.queryValueUnchecked(player, player.getWorld(), toSet.getRegions(), WGEFlags.GIVE_EFFECTS);
        if (!player.getActivePotionEffects().isEmpty()) {
            for (PotionEffect effect: player.getActivePotionEffects()) {
                if (this.give_potions == null || this.give_potions.isEmpty())
                    break;
                for (PotionEffect e: this.give_potions) {
                    if (effect.getType() == e.getType()) {
                        this.map.put(effect.getType(),
                                new PotionEffectDetails(effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()));
                        player.removePotionEffect(effect.getType());
                        break;
                    }
                }
            }
        }
        this.handleValue(player, this.give_potions);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet toSet, Set<PotionEffect> lastValue, MoveType moveType) {
        final Player player = WGEFUtils.wrapPlayer(localPlayer);
        if (!player.getActivePotionEffects().isEmpty()) {
            if (this.give_potions != null && !this.give_potions.isEmpty()) {
                this.give_potions.forEach(e -> {
                    if (player.hasPotionEffect(e.getType()))
                        player.removePotionEffect(e.getType());
                });
            }
        }
        this.give_potions = WGEFUtils.queryValueUnchecked(player, player.getWorld(), toSet.getRegions(), WGEFlags.GIVE_EFFECTS);
        this.handleValue(player, this.give_potions);
        return true;
    }

    @Override
    public void tick(LocalPlayer localPlayer, ApplicableRegionSet set) {
        final Player player = WGEFUtils.wrapPlayer(localPlayer);
        this.give_potions = WGEFUtils.queryValueUnchecked(player, player.getWorld(), set.getRegions(), WGEFlags.GIVE_EFFECTS);
        this.handleValue(player, this.give_potions);
    }

    public void handleValue(Player player, Set<PotionEffect> effects) {
        if (effects == null || effects.isEmpty()) {
            if (!map.isEmpty()) {
                this.map.forEach((type, details) ->
                        player.addPotionEffect(new PotionEffect(type, details.getTimeLeftInTicks(), details.getAmplifier(), details.isAmbient(), details.isParticles())));
                this.map.clear();
            }
            return;
        }
        effects.forEach(player::addPotionEffect);
    }

}
