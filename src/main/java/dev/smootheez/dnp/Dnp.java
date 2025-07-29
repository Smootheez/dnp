package dev.smootheez.dnp;

import net.fabricmc.api.*;

@Environment(EnvType.CLIENT)
public class Dnp implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOGGER.info("Ghast Autopilot initialized");
    }
}
