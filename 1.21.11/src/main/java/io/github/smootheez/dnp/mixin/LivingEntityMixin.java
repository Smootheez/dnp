package io.github.smootheez.dnp.mixin;

import io.github.smootheez.dnp.handler.*;
import net.fabricmc.api.*;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void onDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // We only care about the health data changing on the client
        // Let our dedicated handler manage the logic and state
        if (entity.level().isClientSide() && LivingEntityAccessor.getHealthId().equals(key))
            HandleTextParticle.onHealthChange(entity);
    }
}

