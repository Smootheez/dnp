package dev.smootheez.dnp.mixin;

import dev.smootheez.dnp.particle.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private float lastHealth;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        Level level = entity.level();
        if (level == null || !level.isClientSide()) return;

        float oldHealth = lastHealth;
        float health = entity.getHealth();

        if (oldHealth != health) {
            lastHealth = health;
            DamageNumberParticleContract numberParticle = new DamageNumberParticleImpl();
            numberParticle.handleEntityHealthChange(entity, oldHealth, health);
        }
    }
}
