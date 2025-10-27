package io.github.smootheez.dnp.handler;

import io.github.smootheez.dnp.particle.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;

import java.util.*;

@Environment(EnvType.CLIENT)
public class RenderDamageNumber {
    private final Minecraft minecraft = Minecraft.getInstance();
    private final Deque<NumberParticle> particles = new ArrayDeque<>();

    public void renderParticleNumber(LivingEntity entity, float oldHealth, float newHealth) {
        ClientLevel level = (ClientLevel) entity.level();

        double yOffset = Mth.clamp(entity.getBbHeight() * 0.55, 1.0, 4.0);
        Vec3 position = entity.position().add(0.0, yOffset, 0.0);
        Vec3 velocity = computeVelocity(level, entity, position);

        float diff = oldHealth - newHealth;
        if (diff == 0) return;

        float baseScale = 0.02F;
        float scaleMultiplier = Mth.sqrt(entity.getBbHeight());
        float scaled = baseScale * scaleMultiplier;

        NumberParticle particle = new NumberParticle(level, position, velocity, String.format("%.0f", Math.abs(diff)), Mth.clamp(scaled, 0.02F, 0.045F));

        particles.add(particle);
        minecraft.particleEngine.add(particle);
    }

    private Vec3 computeVelocity(ClientLevel level, LivingEntity entity, Vec3 particlePos) {
        if (level == null) return Vec3.ZERO;

        double spread = 0.05;
        double randomX = (level.random.nextDouble() - 0.5) * spread;
        double randomZ = (level.random.nextDouble() - 0.5) * spread;
        double upward = 0.1;
        Vec3 velocity = new Vec3(randomX, upward, randomZ);

        Vec3 backward = minecraft.gameRenderer.getMainCamera().getPosition()
                .subtract(particlePos)
                .normalize()
                .scale(entity.getBbWidth() * 0.5);

        return velocity.add(backward.x, 0.1, backward.z);
    }

}
