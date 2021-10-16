package io.github.invvk.wgef.abstraction.flags.data;

import java.util.concurrent.TimeUnit;

public record PotionEffectDetails(long getEndTime, int getAmplifier, boolean isAmbient, boolean isParticles) {

    public PotionEffectDetails(int duration, int amplifier, boolean isAmbient, boolean isParticles) {
        this(System.nanoTime() + (long) (duration
                / 20D * TimeUnit.SECONDS.toNanos(1L)), amplifier, isAmbient, isParticles);
    }

    public long getTimeLeft() {
        return this.getEndTime() - System.nanoTime();
    }

    public int getTimeLeftInTicks() {
        return (int) (this.getTimeLeft() / TimeUnit.MILLISECONDS.toNanos(50L));
    }

}
