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
    private final Deque<DamageNumberParticle> particles = new ArrayDeque<>();

    @Override
    public void handleEntityHealthChange(LivingEntity entity, float oldHealth, float newHealth) {
        boolean enableDnp = DnpConfig.ENABLE_DNP.getValue();
        if (!enableDnp) return;

        float healthDiff = newHealth - oldHealth;
        if (healthDiff == 0.0F) return;

        boolean isHealing = healthDiff > 0.0F;
        float value = Math.abs(healthDiff);

        boolean enableHealing = DnpConfig.HEALING.getValue();
        if (isHealing && !enableHealing) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || level != entity.level()) return;

        boolean disableSelfDamage = DnpConfig.SELF_DAMAGE.getValue();
        if (entity == player && disableSelfDamage) return; // optional: ignore self damage

        if (entity.distanceToSqr(player) > 2304.0) return; // 48 block range

        int particleLimit = switch (mc.options.particles().get()) {
            case ALL -> 256;
            case DECREASED -> 64;
            case MINIMAL -> 16;
        };

        while (particles.size() >= particleLimit) {
            var old = particles.poll();
            if (old != null) old.remove();
        }

        double yOffset = Mth.clamp(entity.getBbHeight() * 0.55, 1.0, 4.0);
        Vec3 particlePos = entity.position().add(0.0, yOffset, 0.0);

        // Randomized floaty motion
        double spread = 0.05;
        double randomX = (mc.level.random.nextDouble() - 0.5) * spread;
        double randomZ = (mc.level.random.nextDouble() - 0.5) * spread;
        double upward = 0.1;
        Vec3 particleVelocity = new Vec3(randomX, upward, randomZ);

        // Push away from camera
        Vec3 backward = mc.gameRenderer.getMainCamera().getPosition().subtract(particlePos).normalize();
        backward = backward.scale(entity.getBbWidth() * 0.5); // adjust this value to control spread
        particleVelocity = particleVelocity.add(backward.x, 0.1, backward.z);


        // ✅ Replace TextParticle with your DamageNumberParticle
        var particle = new DamageNumberParticle(level, particlePos, particleVelocity);

        float baseScale = 0.02F;
        float scaleMultiplier = Mth.sqrt(entity.getBbHeight()); // Less aggressive than linear
        float scaled = baseScale * scaleMultiplier;
        particle.setInitialScale(Mth.clamp(scaled, 0.02F, 0.045F));

        // Format text
        double damageInt = Math.round(value);
        double damageThreshold = DnpConfig.DAMAGE_THRESHOLD.getValue();
        int damageColor = damageInt >= damageThreshold ? 0xFF0000 : 0xFFFF00; // red for high damage, yellow for low
        particle.setColor(damageColor);

        String text = String.format("%.1f", value);
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }
        if (isHealing) {
            text = "+" + text;
            particle.setColor(0x00FF00);
        }
        particle.setDamageDealt(text);

        particles.add(particle);
        mc.particleEngine.add(particle);
    }
}

