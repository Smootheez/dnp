package dev.smootheez.dnp.particle;

import net.minecraft.world.entity.*;

public interface DamageNumberParticleContract {
    void handleEntityHealthChange(LivingEntity entity, float oldHealth, float newHealth);
}
