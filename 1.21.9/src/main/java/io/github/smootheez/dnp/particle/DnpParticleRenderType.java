package io.github.smootheez.dnp.particle;

import net.fabricmc.api.*;
import net.minecraft.client.particle.*;

@Environment(EnvType.CLIENT)
public final class DnpParticleRenderType {
    private DnpParticleRenderType() {}

    public static final ParticleRenderType NUMBER_RENDER = new ParticleRenderType("number_render");
}
