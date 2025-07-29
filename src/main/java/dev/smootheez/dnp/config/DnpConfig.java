package dev.smootheez.dnp.config;

import dev.smootheez.dnp.*;
import dev.smootheez.scl.api.*;
import dev.smootheez.scl.config.*;
import net.fabricmc.api.*;

@Environment(EnvType.CLIENT)
@Config(name = Constants.MOD_ID, gui = true)
public class DnpConfig {
    public static final ConfigOption<Boolean> ENABLE_DNP = ConfigOption.create("enableDnp", true);
    public static final ConfigOption<Boolean> SELF_DAMAGE = ConfigOption.create("selfParticles", true);
    public static final ConfigOption<Boolean> HEALING = ConfigOption.create("healing", true);
    public static final ConfigOption<Double> DAMAGE_THRESHOLD = ConfigOption.create("damageThreshold", 10.0, 3.0, 50.0);
}
