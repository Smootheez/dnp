package io.github.smootheez.dnp.mixin.client;

import io.github.smootheez.dnp.particle.*;
import io.github.smootheez.dnp.util.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private float lastHealth;

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        Level level = entity.level();
        if (level == null || !level.isClientSide()) return;

        float oldHealth = lastHealth;
        float health = entity.getHealth();

        if (oldHealth == health) return;

        lastHealth = health;
        DebugMode.sendLoggerInfo("Old Health: " + oldHealth + ", New Health: " + health);

        float baseScale = 0.02F;
        float scaleMultiplier = Mth.sqrt(entity.getBbHeight());
        float scaled = baseScale * scaleMultiplier;

        double yOffset = Mth.clamp(entity.getBbHeight() * 0.55, 1.0, 4.0);
        Vec3 position = entity.position().add(0.0, yOffset, 0.0);
        Vec3 velocity = computeVelocity((ClientLevel) level, entity, position);

        Minecraft.getInstance().particleEngine.add(
                new NumberParticle((ClientLevel) level, position, velocity, String.format("%.0f", health), Mth.clamp(scaled, 0.02F, 0.045F)));
    }

    @Unique
    private Vec3 computeVelocity(ClientLevel level, LivingEntity entity, Vec3 particlePos) {
        if (level == null) return Vec3.ZERO;

        double spread = 0.05;
        double randomX = (level.random.nextDouble() - 0.5) * spread;
        double randomZ = (level.random.nextDouble() - 0.5) * spread;
        double upward = 0.1;
        Vec3 velocity = new Vec3(randomX, upward, randomZ);

        Vec3 backward = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()
                .subtract(particlePos)
                .normalize()
                .scale(entity.getBbWidth() * 0.5);

        return velocity.add(backward.x, 0.1, backward.z);
    }
}
