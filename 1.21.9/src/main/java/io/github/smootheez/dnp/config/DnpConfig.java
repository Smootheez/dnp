package io.github.smootheez.dnp.config;

import io.github.smootheez.dnp.util.*;
import io.github.smootheez.smoothiezapi.api.*;
import io.github.smootheez.smoothiezapi.config.option.*;

@Config(name = Constants.MOD_ID, autoGui = true)
public class DnpConfig implements ConfigApi {
    private final BooleanOption debugMode = new BooleanOption("debug_mode", false);

    private final BooleanOption enableDnp = new BooleanOption("enable_dnp", true);
    private final BooleanOption selfParticle = new BooleanOption("self_particle", false);

    private final DoubleOption damageThreshold = new DoubleOption("damage_threshold", 10.0, 3.0, 50.0);

    private final IntegerOption particleRadius = new IntegerOption("particle_radius", 32, 16, 128);

    private final ListOption blacklist = new ListOption("blacklist");

    public BooleanOption getDebugMode() {
        return debugMode;
    }

    public BooleanOption getEnableDnp() {
        return enableDnp;
    }

    public BooleanOption getSelfParticle() {
        return selfParticle;
    }

    public DoubleOption getDamageThreshold() {
        return damageThreshold;
    }

    public IntegerOption getParticleRadius() {
        return particleRadius;
    }

    public ListOption getBlacklist() {
        return blacklist;
    }
}
