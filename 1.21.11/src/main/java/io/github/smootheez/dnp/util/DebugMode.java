package io.github.smootheez.dnp.util;

import io.github.smootheez.dnp.config.*;
import io.github.smootheez.smoothiezapi.config.*;

public final class DebugMode {
    private DebugMode() {}

    public static void sendLoggerInfo(String msg) {
        if (Boolean.TRUE.equals(ConfigManager.getConfig(DnpConfig.class).getDebugMode().getValue()))
            Constants.LOGGER.info(msg);
    }
}
