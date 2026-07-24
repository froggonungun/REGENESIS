package ru.jgers.regenesis.event.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public class LimboDimensionEffects extends DimensionSpecialEffects {
    public LimboDimensionEffects() {
        super(Float.NaN, false, SkyType.NORMAL, false, false);
    }
    
    @Override
    public float[] getSunriseColor(float timeOfDay, float partialTick) {
        return null;
    }
    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 baseColor, float brightness) {
        return baseColor;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }
}
