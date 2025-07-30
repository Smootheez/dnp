package dev.smootheez.dnp.particle;

import dev.smootheez.dnp.config.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.player.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

import java.util.*;

public class DamageNumberParticleImpl implements DamageNumberParticleContract {
    // Queue to keep track of active particles so we can limit them later based on the user's graphics settings
    private final Deque<DamageNumberParticle> particles = new ArrayDeque<>();

    @Override
    public void handleEntityHealthChange(LivingEntity entity, float oldHealth, float newHealth) {
        // Check if the particle feature is enabled in config
        boolean enableConfig = DnpConfig.ENABLE_DNP.getValue();
        if (!enableConfig) return;

        // Calculate how much health changed
        float healthDiff = newHealth - oldHealth;
        if (healthDiff == 0.0F) return; // Skip if no health change

        boolean isHealing = healthDiff > 0.0F; // Check if it's healing (positive diff)
        float value = Math.abs(healthDiff);    // Use absolute value for display

        // Skip if healing and healing display is disabled in config
        boolean showHealing = DnpConfig.HEALING.getValue();
        if (isHealing && !showHealing) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;

        // Ensure world is valid and the entity is in the same world as the client
        if (level == null || level != entity.level()) return;
        if (shouldSkipEntity(entity, player)) return; // Skip if entity is blacklisted or too far

        ensureParticleLimit(mc); // Remove old particles if we hit limit

        // Determine Y offset to place the particle above the entity
        double yOffset = Mth.clamp(entity.getBbHeight() * 0.55, 1.0, 4.0);
        Vec3 particlePos = entity.position().add(0.0, yOffset, 0.0); // Above the entity's head
        Vec3 particleVelocity = computeVelocity(mc, entity, particlePos); // Particle float direction

        // Create the particle
        var particle = new DamageNumberParticle(level, particlePos, particleVelocity);

        // Scale the particle size based on entity height (so it's readable on both small and big entities)
        float baseScale = 0.02F;
        float scaleMultiplier = Mth.sqrt(entity.getBbHeight());
        float scaled = baseScale * scaleMultiplier;
        particle.setInitialScale(Mth.clamp(scaled, 0.02F, 0.045F)); // Clamp to reasonable size

        // Decide particle color based on how large the damage is
        double damageInt = Math.round(value);
        double damageThreshold = DnpConfig.DAMAGE_THRESHOLD.getValue(); // Threshold for red damage numbers
        int damageColor = damageInt >= damageThreshold ? 0xFF0000 : 0xFFFF00; // Red or yellow
        particle.setColor(damageColor);

        // Format the damage text (e.g., 2.0 -> "2", 1.5 -> "1.5")
        String text = String.format("%.1f", value);
        if (text.endsWith(".0")) text = text.substring(0, text.length() - 2); // Remove decimal if it's .0

        // Add a "+" prefix and set color if it's healing
        if (isHealing) {
            text = "+" + text;
            particle.setColor(0x00FF00); // Green for healing
        }

        particle.setDamageDealt(text); // Set text to render

        // Track particle so we can limit them
        particles.add(particle);
        // Add particle to Minecraft's particle engine so it shows up
        mc.particleEngine.add(particle);
    }

    // Determines if a particle should be skipped for this entity
    private boolean shouldSkipEntity(LivingEntity entity, LocalPlayer player) {
        boolean selfParticles = DnpConfig.SELF_PARTICLES.getValue();
        if (entity == player && selfParticles) return true; // Skip self damage if configured

        int maxDistance = DnpConfig.PARTICLE_RADIUS.getValue();
        if (entity.distanceToSqr(player) > maxDistance * maxDistance) return true; // Skip far entities

        // Check if the entity is blacklisted
        ResourceLocation entityType = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return DnpConfig.BLACKLIST.getValue().values().contains(entityType.toString());
    }

    // Ensures that the number of active particles does not exceed the user's graphics settings
    private void ensureParticleLimit(Minecraft mc) {
        int particleLimit = switch (mc.options.particles().get()) {
            case ALL -> 256;
            case DECREASED -> 64;
            case MINIMAL -> 16;
        };

        // Remove the oldest particles until we are under the limit
        while (particles.size() >= particleLimit) {
            var old = particles.poll(); // Remove the oldest
            if (old != null) old.remove(); // Remove it from the world
        }
    }

    // Computes the velocity vector that determines the direction and speed the particle will move
    private Vec3 computeVelocity(Minecraft mc, LivingEntity entity, Vec3 particlePos) {
        ClientLevel level = mc.level;
        if (level == null) return Vec3.ZERO;

        double spread = 0.05;
        // Slight random horizontal movement
        double randomX = (level.random.nextDouble() - 0.5) * spread;
        double randomZ = (level.random.nextDouble() - 0.5) * spread;
        double upward = 0.1; // Small upward movement
        Vec3 velocity = new Vec3(randomX, upward, randomZ);

        // Move slightly away from camera to improve visibility and add a 3D floating effect
        Vec3 backward = mc.gameRenderer.getMainCamera().getPosition()
                .subtract(particlePos)
                .normalize()
                .scale(entity.getBbWidth() * 0.5);

        // Combine upward, random spread, and slight camera-direction offset
        return velocity.add(backward.x, 0.1, backward.z);
    }
}
