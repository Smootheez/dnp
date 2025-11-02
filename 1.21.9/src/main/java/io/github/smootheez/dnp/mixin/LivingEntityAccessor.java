package io.github.smootheez.dnp.mixin;

import net.fabricmc.api.*;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("DATA_HEALTH_ID")
    static EntityDataAccessor<Float> getHealthId() {
        throw new AssertionError();
    }
}
