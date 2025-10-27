package io.github.smootheez.dnp.particle;

import com.mojang.blaze3d.vertex.*;
import io.github.smootheez.dnp.util.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.culling.*;
import net.minecraft.client.renderer.state.*;
import org.jetbrains.annotations.*;

@Environment(EnvType.CLIENT)
public class TextParticleGroup extends ParticleGroup<TextParticle> {
    public TextParticleGroup(ParticleEngine particleEngine) {
        super(particleEngine);
    }

    @Override
    public @NotNull ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float f) {
        return (submitNodeCollector, cameraRenderState) -> {
            DebugMode.sendLoggerInfo("Rendering NumberParticleGroup");
            Minecraft instance = Minecraft.getInstance();
            PoseStack poseStack = new PoseStack();
            this.particles.forEach(particle -> {
                particle.extract(instance.renderBuffers().bufferSource(), poseStack, instance.font, camera, f);
                DebugMode.sendLoggerInfo("Rendering particle: " + particle);
            });
        };
    }
}
