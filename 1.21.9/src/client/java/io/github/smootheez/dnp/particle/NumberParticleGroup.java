package io.github.smootheez.dnp.particle;

import io.github.smootheez.dnp.util.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.culling.*;
import net.minecraft.client.renderer.state.*;
import org.jetbrains.annotations.*;

@Environment(EnvType.CLIENT)
public class NumberParticleGroup extends ParticleGroup<NumberParticle> {
    public NumberParticleGroup(ParticleEngine particleEngine) {
        super(particleEngine);
    }

    @Override
    public @NotNull ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float f) {
        return (submitNodeCollector, cameraRenderState) -> {
            DebugMode.sendLoggerInfo("Rendering NumberParticleGroup");
        };
    }
}
