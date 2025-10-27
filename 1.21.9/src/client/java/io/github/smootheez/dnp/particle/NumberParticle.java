package io.github.smootheez.dnp.particle;

import io.github.smootheez.dnp.util.*;
import net.fabricmc.api.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.*;

@Environment(EnvType.CLIENT)
public class NumberParticle extends Particle {
    public NumberParticle(ClientLevel clientLevel, Vec3 pos, Vec3 velocity) {
        super(clientLevel, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        DebugMode.sendLoggerInfo("NumberParticle created at " + pos + " with velocity " + velocity);
    }

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return DnpParticleRenderType.NUMBER_RENDER;
    }
}

