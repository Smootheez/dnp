package io.github.smootheez.dnp.handler;

import io.github.smootheez.dnp.config.*;
import io.github.smootheez.dnp.particle.*;
import io.github.smootheez.smoothiezapi.config.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.player.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

import java.util.*;

@Environment(EnvType.CLIENT)
public final class HandleTextParticle {
    private HandleTextParticle() {}

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static final Deque<TextParticle> PARTICLES = new ArrayDeque<>();
    private static final DnpConfig DNP_CONFIG = ConfigManager.getConfig(DnpConfig.class);

    public static void renderParticleNumber(LivingEntity entity, float oldHealth, float newHealth) {
        if (Boolean.FALSE.equals(DNP_CONFIG.getEnableDnp().getValue())) return;

        ClientLevel level = (ClientLevel) entity.level();

        if (shouldSkipRendering(entity)) return;
        ensureParticleLimit();

        float bbHeight = entity.getBbHeight();
        double yOffset = Mth.clamp(bbHeight * 0.55, 1.0, 4.0);
        Vec3 position = entity.position().add(0.0, yOffset, 0.0);
        Vec3 velocity = computeVelocity(level, entity, position);

        float diff = oldHealth - newHealth;
        if (diff == 0) return;

        float baseScale = 0.023F;
        float scaleMultiplier = Mth.sqrt(bbHeight);
        float scaled = baseScale * scaleMultiplier;

        // ----- Dynamic Color Computation -----
        float amount = Math.abs(diff);
        float maxChange = DNP_CONFIG.getDamageThreshold().getValue().floatValue(); // you can adjust this threshold for your mod
        float intensity = Mth.clamp(amount / maxChange, 0.0F, 1.0F);

        // Base colors
        int yellow = 0xFFFF00; // small change
        int red = 0xFF0000;    // large damage
        int green = 0x00FF00;  // large heal

        int color;
        String healthChangeValue = String.format("%.1f", amount);

        if (diff > 0) {
            // Damage → blend yellow → red
            color = lerpColor(yellow, red, intensity);
            healthChangeValue = "-" + healthChangeValue;
        } else {
            // Heal → blend yellow → green
            color = lerpColor(yellow, green, intensity);
            healthChangeValue = "+" + healthChangeValue;
        }

        TextParticle particle = new TextParticle(level, position, velocity,
                healthChangeValue, Mth.clamp(scaled, 0.02F, 0.045F), color);

        PARTICLES.add(particle);
        MINECRAFT.particleEngine.add(particle);
    }

    private static boolean shouldSkipRendering(LivingEntity entity) {
        LocalPlayer player = MINECRAFT.player;
        if (entity == player && Boolean.TRUE.equals(DNP_CONFIG.getSelfParticle().getValue())) return true;

        ResourceLocation entityType = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (DNP_CONFIG.getBlacklist().getValue().values().contains(entityType.toString())) return true;

        int maxDistance = DNP_CONFIG.getParticleRadius().getValue(); // Default max distance is 32
        return entity.distanceToSqr(player) > maxDistance * maxDistance;
    }

    /**
     * Linearly interpolates between two RGB colors.
     * @param from starting color (ARGB or RGB)
     * @param to ending color
     * @param t interpolation factor 0.0–1.0
     * @return blended color as int
     */
    private static int lerpColor(int from, int to, float t) {
        int r1 = (from >> 16) & 0xFF;
        int g1 = (from >> 8) & 0xFF;
        int b1 = from & 0xFF;

        int r2 = (to >> 16) & 0xFF;
        int g2 = (to >> 8) & 0xFF;
        int b2 = to & 0xFF;

        int r = (int) Mth.lerp(t, r1, r2);
        int g = (int) Mth.lerp(t, g1, g2);
        int b = (int) Mth.lerp(t, b1, b2);

        return (r << 16) | (g << 8) | b;
    }

    private static void ensureParticleLimit() {
        int particleLimit = switch (MINECRAFT.options.particles().get()) {
            case ALL -> 255;
            case DECREASED -> 127;
            case MINIMAL -> 63;
        };

        while (PARTICLES.size() > particleLimit) {
            TextParticle oldestParticle = PARTICLES.poll();
            if (oldestParticle != null) oldestParticle.remove();
        }
    }

    private static Vec3 computeVelocity(ClientLevel level, LivingEntity entity, Vec3 particlePos) {
        if (level == null) return Vec3.ZERO;

        double spread = 0.05;
        double randomX = (level.random.nextDouble() - 0.5) * spread;
        double randomZ = (level.random.nextDouble() - 0.5) * spread;
        double upward = 0.1;
        Vec3 velocity = new Vec3(randomX, upward, randomZ);

        Vec3 backward = MINECRAFT.gameRenderer.getMainCamera().getPosition()
                .subtract(particlePos)
                .normalize()
                .scale(entity.getBbWidth() * 0.5);

        return velocity.add(backward.x, 0.1, backward.z);
    }

}
