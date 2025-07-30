package dev.smootheez.dnp.particle;

import dev.smootheez.dnp.config.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.player.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

import java.util.*;

public class DamageNumberParticleImpl implements DamageNumberParticleContract {
    // Queue to keep track of active particles so we can limit them
    private final Deque<DamageNumberParticle> particles = new ArrayDeque<>();

    @Override
    public void handleEntityHealthChange(LivingEntity entity, float oldHealth, float newHealth) {
        // Config check: is DNP (Damage Number Particle) enabled?
        boolean enableConfig = DnpConfig.ENABLE_DNP.getValue();
        if (!enableConfig) return;

        float healthDiff = newHealth - oldHealth;
        if (healthDiff == 0.0F) return; // No change in health, skip

        boolean isHealing = healthDiff > 0.0F;
        float value = Math.abs(healthDiff);

        // Config check: is healing particle enabled?
        boolean showHealing = DnpConfig.HEALING.getValue();
        if (isHealing && !showHealing) return;

        // Get game instance
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;

        // Ensure we are in the same world as the entity
        if (level == null || level != entity.level()) return;

        // Config check: ignore self-damage if disabled
        boolean ignoreSelfParticles = DnpConfig.SELF_DAMAGE.getValue();
        if (entity == player && ignoreSelfParticles) return;

        // Skip if entity is too far from the player (range is squared for optimization)
        double maxDistance = DnpConfig.PARTICLE_RADIUS.getValue();
        if (entity.distanceToSqr(player) > maxDistance * maxDistance) return;

        // Determine how many particles are allowed based on user's particle settings
        int particleLimit = switch (mc.options.particles().get()) {
            case ALL -> 256;
            case DECREASED -> 64;
            case MINIMAL -> 16;
        };

        // If we exceed the limit, remove the oldest particle
        while (particles.size() >= particleLimit) {
            var old = particles.poll();
            if (old != null) old.remove();
        }

        // Compute position above the entity's center height
        double yOffset = Mth.clamp(entity.getBbHeight() * 0.55, 1.0, 4.0);
        Vec3 particlePos = entity.position().add(0.0, yOffset, 0.0);

        // Apply random upward velocity and slight sideward offset
        double spread = 0.05;
        double randomX = (level.random.nextDouble() - 0.5) * spread;
        double randomZ = (level.random.nextDouble() - 0.5) * spread;
        double upward = 0.1;
        Vec3 particleVelocity = new Vec3(randomX, upward, randomZ);

        // Slightly push the particle away from camera direction
        Vec3 backward = mc.gameRenderer.getMainCamera().getPosition()
                .subtract(particlePos)
                .normalize()
                .scale(entity.getBbWidth() * 0.5); // adjust for spread
        particleVelocity = particleVelocity.add(backward.x, 0.1, backward.z);

        // Create the particle instance
        var particle = new DamageNumberParticle(level, particlePos, particleVelocity);

        // Scale the particle size based on entity height (smaller mobs get smaller numbers)
        float baseScale = 0.02F;
        float scaleMultiplier = Mth.sqrt(entity.getBbHeight()); // smoother scaling
        float scaled = baseScale * scaleMultiplier;
        particle.setInitialScale(Mth.clamp(scaled, 0.02F, 0.045F));

        // Choose color: red for high damage, yellow otherwise
        double damageInt = Math.round(value);
        double damageThreshold = DnpConfig.DAMAGE_THRESHOLD.getValue();
        int damageColor = damageInt >= damageThreshold ? 0xFF0000 : 0xFFFF00;
        particle.setColor(damageColor);

        // Format the number string (remove decimal if .0)
        String text = String.format("%.1f", value);
        if (text.endsWith(".0")) text = text.substring(0, text.length() - 2);

        // Add "+" and green color if it's healing
        if (isHealing) {
            text = "+" + text;
            particle.setColor(0x00FF00);
        }

        particle.setDamageDealt(text);

        // Register particle
        particles.add(particle);
        mc.particleEngine.add(particle);
    }
}
