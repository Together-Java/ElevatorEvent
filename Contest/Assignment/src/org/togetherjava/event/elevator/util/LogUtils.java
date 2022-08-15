package org.togetherjava.event.elevator.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LogUtils {
    private static final Logger logger  = LogManager.getLogger();

    private LogUtils() {}
    public static void measure(String name, Runnable toLog) {
        measure(name, toLog, Level.INFO);
    }

    /**
     * If the specified log level is enabled, measure the execution time of the specified runnable,
     * then output the result at that logging level. Otherwise, just execute the runnable.
     */
    public static void measure(String name, Runnable toLog, Level level) {
        if (logger.isEnabled(level)) {
            long start = System.nanoTime();
            toLog.run();
            long end = System.nanoTime();
            logger.log(level, "%s took %s".formatted(name, formatNanos(end - start)));
        } else {
            toLog.run();
        }
    }

    private static String formatNanos(long nanos) {
        if (nanos >= 10_000_000) {
            return "%,.3f s".formatted(nanos / 1e9);
        } else {
            return "%,.3f ms".formatted(nanos / 1e6);
        }
    }
}
