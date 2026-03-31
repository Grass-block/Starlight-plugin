package org.atcraftmc.starlight.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.util.Supplier;

public class ForwardingLogger extends DelegatedLogger {
    public ForwardingLogger(Logger delegate) {
        super(delegate);
    }

    public static ForwardingLogger prefixed(Logger logger, String prefix) {
        return new ForwardingLogger(logger) {
            @Override
            protected String renderForwarding(String msg) {
                return "[" + prefix + "] " + msg;
            }
        };
    }

    // ======================
    // 核心处理逻辑
    // ======================

    protected String renderForwarding(String msg) {
        return msg; // 默认透传
    }


    //debug
    @Override
    public final void debug(String s) {
        super.debug(renderForwarding(s));
    }

    @Override
    public final void debug(Marker marker, String s) {
        super.debug(marker, renderForwarding(s));
    }

    @Override
    public final void debug(Marker marker, String s, Object... objects) {
        super.debug(marker, renderForwarding(s), objects);
    }

    @Override
    public final void debug(Marker marker, String s, Supplier<?>... suppliers) {
        super.debug(marker, renderForwarding(s), suppliers);
    }

    @Override
    public final void debug(Marker marker, String s, Throwable throwable) {
        super.debug(marker, renderForwarding(s), throwable);
    }

    @Override
    public void debug(String s, Object... objects) {
        super.debug(renderForwarding(s), objects);
    }

    @Override
    public void debug(String s, Supplier<?>... suppliers) {
        super.debug(renderForwarding(s), suppliers);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        super.debug(renderForwarding(s), throwable);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        super.debug(marker, renderForwarding(s), o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        super.debug(marker, renderForwarding(s), o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2) {
        super.debug(marker, renderForwarding(s), o, o1, o2);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        super.debug(marker, renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.debug(marker, renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.debug(marker, renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.debug(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.debug(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.debug(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.debug(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void debug(String s, Object o) {
        super.debug(renderForwarding(s), o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        super.debug(renderForwarding(s), o, o1);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2) {
        super.debug(renderForwarding(s), o, o1, o2);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3) {
        super.debug(renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.debug(renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.debug(renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.debug(renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.debug(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.debug(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.debug(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void error(String s, Object o) {
        super.error(renderForwarding(s), o);
    }

    @Override
    public void error(Marker marker, String s) {
        super.error(marker, s);
    }

    @Override
    public void error(Marker marker, String s, Supplier<?>... suppliers) {
        super.error(marker, renderForwarding(s), suppliers);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        super.error(marker, renderForwarding(s), objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        super.error(marker, renderForwarding(s), throwable);
    }

    @Override
    public void error(String s) {
        super.error(renderForwarding(s));
    }

    @Override
    public void error(String s, Object... objects) {
        super.error(renderForwarding(s), objects);
    }

    @Override
    public void error(String s, Supplier<?>... suppliers) {
        super.error(renderForwarding(s), suppliers);
    }

    @Override
    public void error(String s, Throwable throwable) {
        super.error(renderForwarding(s), throwable);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        super.error(marker, renderForwarding(s), o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        super.error(marker, renderForwarding(s), o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2) {
        super.error(marker, renderForwarding(s), o, o1, o2);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        super.error(marker, renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.error(marker, renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.error(marker, renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.error(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.error(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.error(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.error(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        super.error(renderForwarding(s), o, o1);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2) {
        super.error(renderForwarding(s), o, o1, o2);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3) {
        super.error(renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.error(renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.error(renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.error(renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.error(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.error(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.error(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void fatal(Marker marker, String s) {
        super.fatal(marker, s);
    }

    @Override
    public void fatal(Marker marker, String s, Object... objects) {
        super.fatal(marker, renderForwarding(s), objects);
    }

    @Override
    public void fatal(Marker marker, String s, Supplier<?>... suppliers) {
        super.fatal(marker, renderForwarding(s), suppliers);
    }

    @Override
    public void fatal(Marker marker, String s, Throwable throwable) {
        super.fatal(marker, renderForwarding(s), throwable);
    }

    @Override
    public void fatal(String s) {
        super.fatal(renderForwarding(s));
    }

    @Override
    public void fatal(String s, Object... objects) {
        super.fatal(renderForwarding(s), objects);
    }

    @Override
    public void fatal(String s, Supplier<?>... suppliers) {
        super.fatal(renderForwarding(s), suppliers);
    }

    @Override
    public void fatal(String s, Throwable throwable) {
        super.fatal(renderForwarding(s), throwable);
    }

    @Override
    public void fatal(Marker marker, String s, Object o) {
        super.fatal(marker, renderForwarding(s), o);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1) {
        super.fatal(marker, renderForwarding(s), o, o1);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2) {
        super.fatal(marker, renderForwarding(s), o, o1, o2);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        super.fatal(marker, renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.fatal(marker, renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.fatal(marker, renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.fatal(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.fatal(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.fatal(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.fatal(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void fatal(String s, Object o) {
        super.fatal(renderForwarding(s), o);
    }

    @Override
    public void fatal(String s, Object o, Object o1) {
        super.fatal(renderForwarding(s), o, o1);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2) {
        super.fatal(renderForwarding(s), o, o1, o2);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3) {
        super.fatal(renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.fatal(renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.fatal(renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.fatal(renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.fatal(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.fatal(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.fatal(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void info(Marker marker, String s) {
        super.info(marker, renderForwarding(s));
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        super.info(marker, renderForwarding(s), objects);
    }

    @Override
    public void info(Marker marker, String s, Supplier<?>... suppliers) {
        super.info(marker, renderForwarding(s), suppliers);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        super.info(marker, renderForwarding(s), throwable);
    }

    @Override
    public void info(String s) {
        super.info(renderForwarding(s));
    }

    @Override
    public void info(String s, Object... objects) {
        super.info(renderForwarding(s), objects);
    }

    @Override
    public void info(String s, Supplier<?>... suppliers) {
        super.info(renderForwarding(s), suppliers);
    }

    @Override
    public void info(String s, Throwable throwable) {
        super.info(renderForwarding(s), throwable);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        super.info(marker, renderForwarding(s), o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        super.info(marker, renderForwarding(s), o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2) {
        super.info(marker, renderForwarding(s), o, o1, o2);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        super.info(marker, renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.info(marker, renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.info(marker, renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.info(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.info(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.info(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.info(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void info(String s, Object o) {
        super.info(renderForwarding(s), o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        super.info(renderForwarding(s), o, o1);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2) {
        super.info(renderForwarding(s), o, o1, o2);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3) {
        super.info(renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.info(renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.info(renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.info(renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.info(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.info(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.info(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void log(Level level, Marker marker, String s) {
        super.log(level, marker, s);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object... objects) {
        super.log(level, marker, renderForwarding(s), objects);
    }

    @Override
    public void log(Level level, Marker marker, String s, Supplier<?>... suppliers) {
        super.log(level, marker, renderForwarding(s), suppliers);
    }

    @Override
    public void log(Level level, Marker marker, String s, Throwable throwable) {
        super.log(level, marker, renderForwarding(s), throwable);
    }

    @Override
    public void log(Level level, String s) {
        super.log(level, s);
    }

    @Override
    public void log(Level level, String s, Object... objects) {
        super.log(level, renderForwarding(s), objects);
    }

    @Override
    public void log(Level level, String s, Supplier<?>... suppliers) {
        super.log(level, renderForwarding(s), suppliers);
    }

    @Override
    public void log(Level level, String s, Throwable throwable) {
        super.log(level, renderForwarding(s), throwable);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o) {
        super.log(level, marker, renderForwarding(s), o);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2) {
        super.log(level, marker, renderForwarding(s), o, o1, o2);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1) {
        super.log(level, marker, renderForwarding(s), o, o1);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        super.log(level, marker, renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.log(level, marker, renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.log(level, marker, renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.log(level, marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.log(level, marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.log(level, marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.log(level, marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void log(Level level, String s, Object o) {
        super.log(level, renderForwarding(s), o);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1) {
        super.log(level, renderForwarding(s), o, o1);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2) {
        super.log(level, renderForwarding(s), o, o1, o2);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3) {
        super.log(level, renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.log(level, renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.log(level, renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.log(level, renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.log(level, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.log(level, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.log(level, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void printf(Level level, Marker marker, String s, Object... objects) {
        super.printf(level, marker, renderForwarding(s), objects);
    }

    @Override
    public void printf(Level level, String s, Object... objects) {
        super.printf(level, renderForwarding(s), objects);
    }

    @Override
    public void trace(Marker marker, String s) {
        super.trace(marker, s);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        super.trace(marker, renderForwarding(s), objects);
    }

    @Override
    public void trace(Marker marker, String s, Supplier<?>... suppliers) {
        super.trace(marker, renderForwarding(s), suppliers);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        super.trace(marker, renderForwarding(s), throwable);
    }

    @Override
    public void trace(String s) {
        super.trace(renderForwarding(s));
    }

    @Override
    public void trace(String s, Object... objects) {
        super.trace(renderForwarding(s), objects);
    }

    @Override
    public void trace(String s, Supplier<?>... suppliers) {
        super.trace(renderForwarding(s), suppliers);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        super.trace(renderForwarding(s), throwable);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        super.trace(marker, renderForwarding(s), o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        super.trace(marker, renderForwarding(s), o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2) {
        super.trace(marker, renderForwarding(s), o, o1, o2);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        super.trace(marker, renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.trace(marker, renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.trace(marker, renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.trace(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.trace(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.trace(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.trace(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void trace(String s, Object o) {
        super.trace(renderForwarding(s), o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        super.trace(renderForwarding(s), o, o1);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2) {
        super.trace(renderForwarding(s), o, o1, o2);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3) {
        super.trace(renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.trace(renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.trace(renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.trace(renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.trace(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.trace(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.trace(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public EntryMessage traceEntry(String s, Object... objects) {
        return super.traceEntry(renderForwarding(s), objects);
    }

    @Override
    public EntryMessage traceEntry(String s, Supplier<?>... suppliers) {
        return super.traceEntry(renderForwarding(s), suppliers);
    }

    @Override
    public <R> R traceExit(String s, R r) {
        return super.traceExit(renderForwarding(s), r);
    }

    @Override
    public void warn(Marker marker, String s) {
        super.warn(marker, s);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        super.warn(marker, renderForwarding(s), objects);
    }

    @Override
    public void warn(Marker marker, String s, Supplier<?>... suppliers) {
        super.warn(marker, renderForwarding(s), suppliers);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        super.warn(marker, renderForwarding(s), throwable);
    }

    @Override
    public void warn(String s) {
        super.warn(renderForwarding(s));
    }

    @Override
    public void warn(String s, Supplier<?>... suppliers) {
        super.warn(renderForwarding(s), suppliers);
    }

    @Override
    public void warn(String s, Object... objects) {
        super.warn(renderForwarding(s), objects);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        super.warn(renderForwarding(s), throwable);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        super.warn(marker, renderForwarding(s), o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        super.warn(marker, renderForwarding(s), o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2) {
        super.warn(marker, renderForwarding(s), o, o1, o2);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        super.warn(marker, renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.warn(marker, renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.warn(marker, renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.warn(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.warn(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.warn(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.warn(marker, renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void warn(String s, Object o) {
        super.warn(renderForwarding(s), o);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        super.warn(renderForwarding(s), o, o1);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2) {
        super.warn(renderForwarding(s), o, o1, o2);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3) {
        super.warn(renderForwarding(s), o, o1, o2, o3);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        super.warn(renderForwarding(s), o, o1, o2, o3, o4);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        super.warn(renderForwarding(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        super.warn(renderForwarding(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        super.warn(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        super.warn(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        super.warn(renderForwarding(s), o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }
}