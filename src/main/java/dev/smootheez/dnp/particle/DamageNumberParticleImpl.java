package dev.smootheez.dnp.particle;

import dev.smootheez.dnp.config.*;
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
public class DamageNumberParticleImpl implements DamageNumberParticleContract {
    private final Deque<DamageNumberParticle> particles = new ArrayDeque<>();

    @Override
    public void handleEntityHealthChange(LivingEntity entity, float oldHealth, float newHealth) {
        boolean enableConfig = DnpConfig.ENABLE_DNP.getValue();
        if (!enableConfig) return;

        ClientLevel level = (ClientLevel) entity.level();
        Minecraft mc = Minecraft.getInstance();

        LocalPlayer player = mc.player;

        if (shouldSkipEntity(entity, player)) return;
        ensureParticleLimit(mc);

        double yOffset = Mth.clamp(entity.getBbHeight() * 0.55, 1.0, 4.0);
        Vec3 position = entity.position().add(0.0, yOffset, 0.0);
        Vec3 velocity = computeVelocity(mc, entity, position);

        float diff = newHealth - oldHealth;
        if (diff == 0) return;

        boolean showHealing = DnpConfig.HEALING.getValue();
        if (diff > 0 && !showHealing) return;

        int color;
        String text = String.format("%.1f", Math.abs(diff));
        double damageThreshold = DnpConfig.DAMAGE_THRESHOLD.getValue();
        if (diff < 0) {
            text = "-" + text;
            if (diff <= -damageThreshold)
                color = -65536;
            else
                color = -256;
        } else {
            text = "+" + text;
            color = -16711936;
        }

        float baseScale = 0.02F;
        float scaleMultiplier = Mth.sqrt(entity.getBbHeight());
        float scaled = baseScale * scaleMultiplier;
        DamageNumberParticle particle = new DamageNumberParticle(level, position, velocity, text, Mth.clamp(scaled, 0.02F, 0.045F), color);

        particles.add(particle);
        mc.particleEngine.add(particle);
    }

    private void ensureParticleLimit(Minecraft mc) {
        int particleLimit = switch (mc.options.particles().get()) {
            case ALL -> 256;
            case DECREASED -> 64;
            case MINIMAL -> 16;
        };

        while (particles.size() >= particleLimit) {
            var old = particles.poll();
            if (old != null) old.remove();
        }
    }

    private boolean shouldSkipEntity(LivingEntity entity, LocalPlayer player) {
        boolean selfParticles = DnpConfig.SELF_PARTICLES.getValue();
        if (entity == player && selfParticles) return true;

        int maxDistance = DnpConfig.PARTICLE_RADIUS.getValue();
        if (entity.distanceToSqr(player) > maxDistance * maxDistance) return true;

        ResourceLocation entityType = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return DnpConfig.BLACKLIST.getValue().values().contains(entityType.toString());
    }

    private Vec3 computeVelocity(Minecraft mc, LivingEntity entity, Vec3 particlePos) {
        ClientLevel level = mc.level;
        if (level == null) return Vec3.ZERO;

        double spread = 0.05;
        double randomX = (level.random.nextDouble() - 0.5) * spread;
        double randomZ = (level.random.nextDouble() - 0.5) * spread;
        double upward = 0.1;
        Vec3 velocity = new Vec3(randomX, upward, randomZ);

        Vec3 backward = mc.gameRenderer.getMainCamera().getPosition()
                .subtract(particlePos)
                .normalize()
                .scale(entity.getBbWidth() * 0.5);

        return velocity.add(backward.x, 0.1, backward.z);
    }
}
