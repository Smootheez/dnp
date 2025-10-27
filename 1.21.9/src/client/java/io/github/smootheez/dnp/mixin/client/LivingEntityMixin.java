package io.github.smootheez.dnp.mixin.client;

import io.github.smootheez.dnp.handler.*;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private float lastHealth = -1;

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void onDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level level = entity.level();
        if (level == null || !level.isClientSide()) return;

        if (LivingEntity.DATA_HEALTH_ID.equals(key)) {
            float newHealth = entity.getHealth();

            if (lastHealth != -1 && lastHealth != newHealth) {
                RenderDamageNumber renderDamageNumber = new RenderDamageNumber();
                renderDamageNumber.renderParticleNumber(entity, lastHealth, newHealth);
            }

            lastHealth = newHealth;
        }
    }
}

