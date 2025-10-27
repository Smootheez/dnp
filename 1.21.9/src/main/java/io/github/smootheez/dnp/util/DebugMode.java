package io.github.smootheez.dnp.util;

public final class DebugMode {
    private DebugMode() {}

    public static void sendLoggerInfo(String msg) {
        Constants.LOGGER.info(msg);
    }
}
