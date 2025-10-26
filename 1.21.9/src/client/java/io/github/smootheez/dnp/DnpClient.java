package io.github.smootheez.dnp;

import io.github.smootheez.dnp.util.*;
import net.fabricmc.api.*;

@Environment(EnvType.CLIENT)
public class DnpClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOGGER.info("Initializing Client " + Constants.MOD_NAME + "(" + Constants.MOD_ID + ")...");
    }
}
