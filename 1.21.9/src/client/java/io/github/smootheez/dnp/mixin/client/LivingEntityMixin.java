package io.github.smootheez.dnp.mixin.client;

import io.github.smootheez.dnp.handler.*;
import io.github.smootheez.dnp.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
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
    }
}
