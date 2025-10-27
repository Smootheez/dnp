package io.github.smootheez.dnp.particle;

import net.fabricmc.api.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.*;

@Environment(EnvType.CLIENT)
public class NumberParticle extends NoRenderParticle {
    public NumberParticle(ClientLevel clientLevel, Vec3 pos, Vec3 velocity) {
        super(clientLevel, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
    }
}

