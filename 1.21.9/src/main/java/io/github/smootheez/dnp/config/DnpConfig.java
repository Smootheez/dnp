package io.github.smootheez.dnp.config;

import io.github.smootheez.dnp.util.*;
import io.github.smootheez.smoothiezapi.api.*;
import io.github.smootheez.smoothiezapi.config.option.*;

@Config(name = Constants.MOD_ID, autoGui = true)
public class DnpConfig implements ConfigApi {
    private final BooleanOption debugMode = new BooleanOption("debug_mode", false);

    public BooleanOption getDebugMode() {
        return debugMode;
    }
}
