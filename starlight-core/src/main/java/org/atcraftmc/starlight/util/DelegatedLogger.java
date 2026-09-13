package org.atcraftmc.starlight.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

public class DelegatedLogger implements Logger {
    private final Logger logger;

    public DelegatedLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void catching(Level level, Throwable throwable) {
        logger.catching(level, throwable);
    }

    @Override
    public void catching(Throwable throwable) {
        logger.catching(throwable);
    }

    @Override
    public void debug(Marker marker, Message message) {
        logger.debug(marker, message);
    }

    @Override
    public void debug(Marker marker, Message message, Throwable throwable) {
        logger.debug(marker, message, throwable);
    }

    @Override
    public void debug(Marker marker, MessageSupplier messageSupplier) {
        logger.debug(marker, messageSupplier);
    }

    @Override
    public void debug(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logger.debug(marker, messageSupplier, throwable);
    }

    @Override
    public void debug(Marker marker, CharSequence charSequence) {
        logger.debug(marker, charSequence);
    }

    @Override
    public void debug(Marker marker, CharSequence charSequence, Throwable throwable) {
        logger.debug(marker, charSequence, throwable);
    }

    @Override
    public void debug(Marker marker, Object o) {
        logger.debug(marker, o);
    }

    @Override
    public void debug(Marker marker, Object o, Throwable throwable) {
        logger.debug(marker, o, throwable);
    }

    @Override
    public void debug(Marker marker, String s) {
        logger.debug(marker, s);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        logger.debug(marker, s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Supplier<?>... suppliers) {
        logger.debug(marker, s, suppliers);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        logger.debug(marker, s, throwable);
    }

    @Override
    public void debug(Marker marker, Supplier<?> supplier) {
        logger.debug(marker, supplier);
    }

    @Override
    public void debug(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logger.debug(marker, supplier, throwable);
    }

    @Override
    public void debug(Message message) {
        logger.debug(message);
    }

    @Override
    public void debug(Message message, Throwable throwable) {
        logger.debug(message, throwable);
    }

    @Override
    public void debug(MessageSupplier messageSupplier) {
        logger.debug(messageSupplier);
    }

    @Override
    public void debug(MessageSupplier messageSupplier, Throwable throwable) {
        logger.debug(messageSupplier, throwable);
    }

    @Override
    public void debug(CharSequence charSequence) {
        logger.debug(charSequence);
    }

    @Override
    public void debug(CharSequence charSequence, Throwable throwable) {
        logger.debug(charSequence, throwable);
    }

    @Override
    public void debug(Object o) {
        logger.debug(o);
    }

    @Override
    public void debug(Object o, Throwable throwable) {
        logger.debug(o, throwable);
    }

    @Override
    public void debug(String s) {
        logger.debug(s);
    }

    @Override
    public void debug(String s, Object... objects) {
        logger.debug(s, objects);
    }

    @Override
    public void debug(String s, Supplier<?>... suppliers) {
        logger.debug(s, suppliers);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        logger.debug(s, throwable);
    }

    @Override
    public void debug(Supplier<?> supplier) {
        logger.debug(supplier);
    }

    @Override
    public void debug(Supplier<?> supplier, Throwable throwable) {
        logger.debug(supplier, throwable);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        logger.debug(marker, s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        logger.debug(marker, s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2) {
        logger.debug(marker, s, o, o1, o2);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logger.debug(marker, s, o, o1, o2, o3);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.debug(marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.debug(marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.debug(marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.debug(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.debug(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.debug(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void debug(String s, Object o) {
        logger.debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        logger.debug(s, o, o1);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2) {
        logger.debug(s, o, o1, o2);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3) {
        logger.debug(s, o, o1, o2, o3);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.debug(s, o, o1, o2, o3, o4);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.debug(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.debug(s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.debug(s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.debug(s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void debug(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.debug(s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Deprecated
    @Override
    public void entry() {
        logger.entry();
    }

    @Override
    public void entry(Object... objects) {
        logger.entry(objects);
    }

    @Override
    public void error(Marker marker, Message message) {
        logger.error(marker, message);
    }

    @Override
    public void error(Marker marker, Message message, Throwable throwable) {
        logger.error(marker, message, throwable);
    }

    @Override
    public void error(Marker marker, MessageSupplier messageSupplier) {
        logger.error(marker, messageSupplier);
    }

    @Override
    public void error(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logger.error(marker, messageSupplier, throwable);
    }

    @Override
    public void error(Marker marker, CharSequence charSequence) {
        logger.error(marker, charSequence);
    }

    @Override
    public void error(Marker marker, CharSequence charSequence, Throwable throwable) {
        logger.error(marker, charSequence, throwable);
    }

    @Override
    public void error(Marker marker, Object o) {
        logger.error(marker, o);
    }

    @Override
    public void error(Marker marker, Object o, Throwable throwable) {
        logger.error(marker, o, throwable);
    }

    @Override
    public void error(Marker marker, String s) {
        logger.error(marker, s);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        logger.error(marker, s, objects);
    }

    @Override
    public void error(Marker marker, String s, Supplier<?>... suppliers) {
        logger.error(marker, s, suppliers);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        logger.error(marker, s, throwable);
    }

    @Override
    public void error(Marker marker, Supplier<?> supplier) {
        logger.error(marker, supplier);
    }

    @Override
    public void error(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logger.error(marker, supplier, throwable);
    }

    @Override
    public void error(Message message) {
        logger.error(message);
    }

    @Override
    public void error(Message message, Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    public void error(MessageSupplier messageSupplier) {
        logger.error(messageSupplier);
    }

    @Override
    public void error(MessageSupplier messageSupplier, Throwable throwable) {
        logger.error(messageSupplier, throwable);
    }

    @Override
    public void error(CharSequence charSequence) {
        logger.error(charSequence);
    }

    @Override
    public void error(CharSequence charSequence, Throwable throwable) {
        logger.error(charSequence, throwable);
    }

    @Override
    public void error(Object o) {
        logger.error(o);
    }

    @Override
    public void error(Object o, Throwable throwable) {
        logger.error(o, throwable);
    }

    @Override
    public void error(String s) {
        logger.error(s);
    }

    @Override
    public void error(String s, Object... objects) {
        logger.error(s, objects);
    }

    @Override
    public void error(String s, Supplier<?>... suppliers) {
        logger.error(s, suppliers);
    }

    @Override
    public void error(String s, Throwable throwable) {
        logger.error(s, throwable);
    }

    @Override
    public void error(Supplier<?> supplier) {
        logger.error(supplier);
    }

    @Override
    public void error(Supplier<?> supplier, Throwable throwable) {
        logger.error(supplier, throwable);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        logger.error(marker, s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        logger.error(marker, s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2) {
        logger.error(marker, s, o, o1, o2);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logger.error(marker, s, o, o1, o2, o3);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.error(marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.error(marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.error(marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.error(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.error(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.error(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void error(String s, Object o) {
        logger.error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        logger.error(s, o, o1);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2) {
        logger.error(s, o, o1, o2);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3) {
        logger.error(s, o, o1, o2, o3);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.error(s, o, o1, o2, o3, o4);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.error(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.error(s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.error(s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.error(s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void error(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.error(s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Deprecated
    @Override
    public void exit() {
        logger.exit();
    }

    @Deprecated
    @Override
    public <R> R exit(R r) {
        return logger.exit(r);
    }

    @Override
    public void fatal(Marker marker, Message message) {
        logger.fatal(marker, message);
    }

    @Override
    public void fatal(Marker marker, Message message, Throwable throwable) {
        logger.fatal(marker, message, throwable);
    }

    @Override
    public void fatal(Marker marker, MessageSupplier messageSupplier) {
        logger.fatal(marker, messageSupplier);
    }

    @Override
    public void fatal(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logger.fatal(marker, messageSupplier, throwable);
    }

    @Override
    public void fatal(Marker marker, CharSequence charSequence) {
        logger.fatal(marker, charSequence);
    }

    @Override
    public void fatal(Marker marker, CharSequence charSequence, Throwable throwable) {
        logger.fatal(marker, charSequence, throwable);
    }

    @Override
    public void fatal(Marker marker, Object o) {
        logger.fatal(marker, o);
    }

    @Override
    public void fatal(Marker marker, Object o, Throwable throwable) {
        logger.fatal(marker, o, throwable);
    }

    @Override
    public void fatal(Marker marker, String s) {
        logger.fatal(marker, s);
    }

    @Override
    public void fatal(Marker marker, String s, Object... objects) {
        logger.fatal(marker, s, objects);
    }

    @Override
    public void fatal(Marker marker, String s, Supplier<?>... suppliers) {
        logger.fatal(marker, s, suppliers);
    }

    @Override
    public void fatal(Marker marker, String s, Throwable throwable) {
        logger.fatal(marker, s, throwable);
    }

    @Override
    public void fatal(Marker marker, Supplier<?> supplier) {
        logger.fatal(marker, supplier);
    }

    @Override
    public void fatal(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logger.fatal(marker, supplier, throwable);
    }

    @Override
    public void fatal(Message message) {
        logger.fatal(message);
    }

    @Override
    public void fatal(Message message, Throwable throwable) {
        logger.fatal(message, throwable);
    }

    @Override
    public void fatal(MessageSupplier messageSupplier) {
        logger.fatal(messageSupplier);
    }

    @Override
    public void fatal(MessageSupplier messageSupplier, Throwable throwable) {
        logger.fatal(messageSupplier, throwable);
    }

    @Override
    public void fatal(CharSequence charSequence) {
        logger.fatal(charSequence);
    }

    @Override
    public void fatal(CharSequence charSequence, Throwable throwable) {
        logger.fatal(charSequence, throwable);
    }

    @Override
    public void fatal(Object o) {
        logger.fatal(o);
    }

    @Override
    public void fatal(Object o, Throwable throwable) {
        logger.fatal(o, throwable);
    }

    @Override
    public void fatal(String s) {
        logger.fatal(s);
    }

    @Override
    public void fatal(String s, Object... objects) {
        logger.fatal(s, objects);
    }

    @Override
    public void fatal(String s, Supplier<?>... suppliers) {
        logger.fatal(s, suppliers);
    }

    @Override
    public void fatal(String s, Throwable throwable) {
        logger.fatal(s, throwable);
    }

    @Override
    public void fatal(Supplier<?> supplier) {
        logger.fatal(supplier);
    }

    @Override
    public void fatal(Supplier<?> supplier, Throwable throwable) {
        logger.fatal(supplier, throwable);
    }

    @Override
    public void fatal(Marker marker, String s, Object o) {
        logger.fatal(marker, s, o);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1) {
        logger.fatal(marker, s, o, o1);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2) {
        logger.fatal(marker, s, o, o1, o2);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logger.fatal(marker, s, o, o1, o2, o3);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.fatal(marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.fatal(marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.fatal(marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.fatal(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.fatal(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void fatal(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.fatal(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void fatal(String s, Object o) {
        logger.fatal(s, o);
    }

    @Override
    public void fatal(String s, Object o, Object o1) {
        logger.fatal(s, o, o1);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2) {
        logger.fatal(s, o, o1, o2);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3) {
        logger.fatal(s, o, o1, o2, o3);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.fatal(s, o, o1, o2, o3, o4);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.fatal(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.fatal(s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.fatal(s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.fatal(s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void fatal(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.fatal(s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public Level getLevel() {
        return logger.getLevel();
    }

    @Override
    public <MF extends MessageFactory> MF getMessageFactory() {
        return logger.getMessageFactory();
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void info(Marker marker, Message message) {
        logger.info(marker, message);
    }

    @Override
    public void info(Marker marker, Message message, Throwable throwable) {
        logger.info(marker, message, throwable);
    }

    @Override
    public void info(Marker marker, MessageSupplier messageSupplier) {
        logger.info(marker, messageSupplier);
    }

    @Override
    public void info(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logger.info(marker, messageSupplier, throwable);
    }

    @Override
    public void info(Marker marker, CharSequence charSequence) {
        logger.info(marker, charSequence);
    }

    @Override
    public void info(Marker marker, CharSequence charSequence, Throwable throwable) {
        logger.info(marker, charSequence, throwable);
    }

    @Override
    public void info(Marker marker, Object o) {
        logger.info(marker, o);
    }

    @Override
    public void info(Marker marker, Object o, Throwable throwable) {
        logger.info(marker, o, throwable);
    }

    @Override
    public void info(Marker marker, String s) {
        logger.info(marker, s);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        logger.info(marker, s, objects);
    }

    @Override
    public void info(Marker marker, String s, Supplier<?>... suppliers) {
        logger.info(marker, s, suppliers);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        logger.info(marker, s, throwable);
    }

    @Override
    public void info(Marker marker, Supplier<?> supplier) {
        logger.info(marker, supplier);
    }

    @Override
    public void info(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logger.info(marker, supplier, throwable);
    }

    @Override
    public void info(Message message) {
        logger.info(message);
    }

    @Override
    public void info(Message message, Throwable throwable) {
        logger.info(message, throwable);
    }

    @Override
    public void info(MessageSupplier messageSupplier) {
        logger.info(messageSupplier);
    }

    @Override
    public void info(MessageSupplier messageSupplier, Throwable throwable) {
        logger.info(messageSupplier, throwable);
    }

    @Override
    public void info(CharSequence charSequence) {
        logger.info(charSequence);
    }

    @Override
    public void info(CharSequence charSequence, Throwable throwable) {
        logger.info(charSequence, throwable);
    }

    @Override
    public void info(Object o) {
        logger.info(o);
    }

    @Override
    public void info(Object o, Throwable throwable) {
        logger.info(o, throwable);
    }

    @Override
    public void info(String s) {
        logger.info(s);
    }

    @Override
    public void info(String s, Object... objects) {
        logger.info(s, objects);
    }

    @Override
    public void info(String s, Supplier<?>... suppliers) {
        logger.info(s, suppliers);
    }

    @Override
    public void info(String s, Throwable throwable) {
        logger.info(s, throwable);
    }

    @Override
    public void info(Supplier<?> supplier) {
        logger.info(supplier);
    }

    @Override
    public void info(Supplier<?> supplier, Throwable throwable) {
        logger.info(supplier, throwable);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        logger.info(marker, s, o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        logger.info(marker, s, o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2) {
        logger.info(marker, s, o, o1, o2);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logger.info(marker, s, o, o1, o2, o3);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.info(marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.info(marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.info(marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.info(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.info(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.info(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void info(String s, Object o) {
        logger.info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        logger.info(s, o, o1);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2) {
        logger.info(s, o, o1, o2);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3) {
        logger.info(s, o, o1, o2, o3);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.info(s, o, o1, o2, o3, o4);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.info(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.info(s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.info(s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.info(s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void info(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.info(s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public boolean isEnabled(Level level) {
        return logger.isEnabled(level);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker) {
        return logger.isEnabled(level, marker);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isFatalEnabled();
    }

    @Override
    public boolean isFatalEnabled(Marker marker) {
        return logger.isFatalEnabled(marker);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void log(Level level, Marker marker, Message message) {
        logger.log(level, marker, message);
    }

    @Override
    public void log(Level level, Marker marker, Message message, Throwable throwable) {
        logger.log(level, marker, message, throwable);
    }

    @Override
    public void log(Level level, Marker marker, MessageSupplier messageSupplier) {
        logger.log(level, marker, messageSupplier);
    }

    @Override
    public void log(Level level, Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logger.log(level, marker, messageSupplier, throwable);
    }

    @Override
    public void log(Level level, Marker marker, CharSequence charSequence) {
        logger.log(level, marker, charSequence);
    }

    @Override
    public void log(Level level, Marker marker, CharSequence charSequence, Throwable throwable) {
        logger.log(level, marker, charSequence, throwable);
    }

    @Override
    public void log(Level level, Marker marker, Object o) {
        logger.log(level, marker, o);
    }

    @Override
    public void log(Level level, Marker marker, Object o, Throwable throwable) {
        logger.log(level, marker, o, throwable);
    }

    @Override
    public void log(Level level, Marker marker, String s) {
        logger.log(level, marker, s);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object... objects) {
        logger.log(level, marker, s, objects);
    }

    @Override
    public void log(Level level, Marker marker, String s, Supplier<?>... suppliers) {
        logger.log(level, marker, s, suppliers);
    }

    @Override
    public void log(Level level, Marker marker, String s, Throwable throwable) {
        logger.log(level, marker, s, throwable);
    }

    @Override
    public void log(Level level, Marker marker, Supplier<?> supplier) {
        logger.log(level, marker, supplier);
    }

    @Override
    public void log(Level level, Marker marker, Supplier<?> supplier, Throwable throwable) {
        logger.log(level, marker, supplier, throwable);
    }

    @Override
    public void log(Level level, Message message) {
        logger.log(level, message);
    }

    @Override
    public void log(Level level, Message message, Throwable throwable) {
        logger.log(level, message, throwable);
    }

    @Override
    public void log(Level level, MessageSupplier messageSupplier) {
        logger.log(level, messageSupplier);
    }

    @Override
    public void log(Level level, MessageSupplier messageSupplier, Throwable throwable) {
        logger.log(level, messageSupplier, throwable);
    }

    @Override
    public void log(Level level, CharSequence charSequence) {
        logger.log(level, charSequence);
    }

    @Override
    public void log(Level level, CharSequence charSequence, Throwable throwable) {
        logger.log(level, charSequence, throwable);
    }

    @Override
    public void log(Level level, Object o) {
        logger.log(level, o);
    }

    @Override
    public void log(Level level, Object o, Throwable throwable) {
        logger.log(level, o, throwable);
    }

    @Override
    public void log(Level level, String s) {
        logger.log(level, s);
    }

    @Override
    public void log(Level level, String s, Object... objects) {
        logger.log(level, s, objects);
    }

    @Override
    public void log(Level level, String s, Supplier<?>... suppliers) {
        logger.log(level, s, suppliers);
    }

    @Override
    public void log(Level level, String s, Throwable throwable) {
        logger.log(level, s, throwable);
    }

    @Override
    public void log(Level level, Supplier<?> supplier) {
        logger.log(level, supplier);
    }

    @Override
    public void log(Level level, Supplier<?> supplier, Throwable throwable) {
        logger.log(level, supplier, throwable);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o) {
        logger.log(level, marker, s, o);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1) {
        logger.log(level, marker, s, o, o1);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2) {
        logger.log(level, marker, s, o, o1, o2);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logger.log(level, marker, s, o, o1, o2, o3);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.log(level, marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.log(level, marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.log(level, marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.log(level, marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.log(level, marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void log(Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.log(level, marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void log(Level level, String s, Object o) {
        logger.log(level, s, o);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1) {
        logger.log(level, s, o, o1);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2) {
        logger.log(level, s, o, o1, o2);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3) {
        logger.log(level, s, o, o1, o2, o3);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.log(level, s, o, o1, o2, o3, o4);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.log(level, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.log(level, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.log(level, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.log(level, s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void log(Level level, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.log(level, s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void printf(Level level, Marker marker, String s, Object... objects) {
        logger.printf(level, marker, s, objects);
    }

    @Override
    public void printf(Level level, String s, Object... objects) {
        logger.printf(level, s, objects);
    }

    @Override
    public <T extends Throwable> T throwing(Level level, T t) {
        return logger.throwing(level, t);
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        return logger.throwing(t);
    }

    @Override
    public void trace(Marker marker, Message message) {
        logger.trace(marker, message);
    }

    @Override
    public void trace(Marker marker, Message message, Throwable throwable) {
        logger.trace(marker, message, throwable);
    }

    @Override
    public void trace(Marker marker, MessageSupplier messageSupplier) {
        logger.trace(marker, messageSupplier);
    }

    @Override
    public void trace(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logger.trace(marker, messageSupplier, throwable);
    }

    @Override
    public void trace(Marker marker, CharSequence charSequence) {
        logger.trace(marker, charSequence);
    }

    @Override
    public void trace(Marker marker, CharSequence charSequence, Throwable throwable) {
        logger.trace(marker, charSequence, throwable);
    }

    @Override
    public void trace(Marker marker, Object o) {
        logger.trace(marker, o);
    }

    @Override
    public void trace(Marker marker, Object o, Throwable throwable) {
        logger.trace(marker, o, throwable);
    }

    @Override
    public void trace(Marker marker, String s) {
        logger.trace(marker, s);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        logger.trace(marker, s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Supplier<?>... suppliers) {
        logger.trace(marker, s, suppliers);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        logger.trace(marker, s, throwable);
    }

    @Override
    public void trace(Marker marker, Supplier<?> supplier) {
        logger.trace(marker, supplier);
    }

    @Override
    public void trace(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logger.trace(marker, supplier, throwable);
    }

    @Override
    public void trace(Message message) {
        logger.trace(message);
    }

    @Override
    public void trace(Message message, Throwable throwable) {
        logger.trace(message, throwable);
    }

    @Override
    public void trace(MessageSupplier messageSupplier) {
        logger.trace(messageSupplier);
    }

    @Override
    public void trace(MessageSupplier messageSupplier, Throwable throwable) {
        logger.trace(messageSupplier, throwable);
    }

    @Override
    public void trace(CharSequence charSequence) {
        logger.trace(charSequence);
    }

    @Override
    public void trace(CharSequence charSequence, Throwable throwable) {
        logger.trace(charSequence, throwable);
    }

    @Override
    public void trace(Object o) {
        logger.trace(o);
    }

    @Override
    public void trace(Object o, Throwable throwable) {
        logger.trace(o, throwable);
    }

    @Override
    public void trace(String s) {
        logger.trace(s);
    }

    @Override
    public void trace(String s, Object... objects) {
        logger.trace(s, objects);
    }

    @Override
    public void trace(String s, Supplier<?>... suppliers) {
        logger.trace(s, suppliers);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        logger.trace(s, throwable);
    }

    @Override
    public void trace(Supplier<?> supplier) {
        logger.trace(supplier);
    }

    @Override
    public void trace(Supplier<?> supplier, Throwable throwable) {
        logger.trace(supplier, throwable);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        logger.trace(marker, s, o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        logger.trace(marker, s, o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2) {
        logger.trace(marker, s, o, o1, o2);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logger.trace(marker, s, o, o1, o2, o3);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.trace(marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.trace(marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.trace(marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.trace(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.trace(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.trace(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void trace(String s, Object o) {
        logger.trace(s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        logger.trace(s, o, o1);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2) {
        logger.trace(s, o, o1, o2);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3) {
        logger.trace(s, o, o1, o2, o3);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.trace(s, o, o1, o2, o3, o4);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.trace(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.trace(s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.trace(s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.trace(s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void trace(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.trace(s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public EntryMessage traceEntry() {
        return logger.traceEntry();
    }

    @Override
    public EntryMessage traceEntry(String s, Object... objects) {
        return logger.traceEntry(s, objects);
    }

    @Override
    public EntryMessage traceEntry(Supplier<?>... suppliers) {
        return logger.traceEntry(suppliers);
    }

    @Override
    public EntryMessage traceEntry(String s, Supplier<?>... suppliers) {
        return logger.traceEntry(s, suppliers);
    }

    @Override
    public EntryMessage traceEntry(Message message) {
        return logger.traceEntry(message);
    }

    @Override
    public void traceExit() {
        logger.traceExit();
    }

    @Override
    public <R> R traceExit(R r) {
        return logger.traceExit(r);
    }

    @Override
    public <R> R traceExit(String s, R r) {
        return logger.traceExit(s, r);
    }

    @Override
    public void traceExit(EntryMessage entryMessage) {
        logger.traceExit(entryMessage);
    }

    @Override
    public <R> R traceExit(EntryMessage entryMessage, R r) {
        return logger.traceExit(entryMessage, r);
    }

    @Override
    public <R> R traceExit(Message message, R r) {
        return logger.traceExit(message, r);
    }

    @Override
    public void warn(Marker marker, Message message) {
        logger.warn(marker, message);
    }

    @Override
    public void warn(Marker marker, Message message, Throwable throwable) {
        logger.warn(marker, message, throwable);
    }

    @Override
    public void warn(Marker marker, MessageSupplier messageSupplier) {
        logger.warn(marker, messageSupplier);
    }

    @Override
    public void warn(Marker marker, MessageSupplier messageSupplier, Throwable throwable) {
        logger.warn(marker, messageSupplier, throwable);
    }

    @Override
    public void warn(Marker marker, CharSequence charSequence) {
        logger.warn(marker, charSequence);
    }

    @Override
    public void warn(Marker marker, CharSequence charSequence, Throwable throwable) {
        logger.warn(marker, charSequence, throwable);
    }

    @Override
    public void warn(Marker marker, Object o) {
        logger.warn(marker, o);
    }

    @Override
    public void warn(Marker marker, Object o, Throwable throwable) {
        logger.warn(marker, o, throwable);
    }

    @Override
    public void warn(Marker marker, String s) {
        logger.warn(marker, s);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        logger.warn(marker, s, objects);
    }

    @Override
    public void warn(Marker marker, String s, Supplier<?>... suppliers) {
        logger.warn(marker, s, suppliers);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        logger.warn(marker, s, throwable);
    }

    @Override
    public void warn(Marker marker, Supplier<?> supplier) {
        logger.warn(marker, supplier);
    }

    @Override
    public void warn(Marker marker, Supplier<?> supplier, Throwable throwable) {
        logger.warn(marker, supplier, throwable);
    }

    @Override
    public void warn(Message message) {
        logger.warn(message);
    }

    @Override
    public void warn(Message message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    @Override
    public void warn(MessageSupplier messageSupplier) {
        logger.warn(messageSupplier);
    }

    @Override
    public void warn(MessageSupplier messageSupplier, Throwable throwable) {
        logger.warn(messageSupplier, throwable);
    }

    @Override
    public void warn(CharSequence charSequence) {
        logger.warn(charSequence);
    }

    @Override
    public void warn(CharSequence charSequence, Throwable throwable) {
        logger.warn(charSequence, throwable);
    }

    @Override
    public void warn(Object o) {
        logger.warn(o);
    }

    @Override
    public void warn(Object o, Throwable throwable) {
        logger.warn(o, throwable);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }

    @Override
    public void warn(String s, Object... objects) {
        logger.warn(s, objects);
    }

    @Override
    public void warn(String s, Supplier<?>... suppliers) {
        logger.warn(s, suppliers);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        logger.warn(s, throwable);
    }

    @Override
    public void warn(Supplier<?> supplier) {
        logger.warn(supplier);
    }

    @Override
    public void warn(Supplier<?> supplier, Throwable throwable) {
        logger.warn(supplier, throwable);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        logger.warn(marker, s, o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        logger.warn(marker, s, o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2) {
        logger.warn(marker, s, o, o1, o2);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        logger.warn(marker, s, o, o1, o2, o3);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.warn(marker, s, o, o1, o2, o3, o4);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.warn(marker, s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.warn(marker, s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.warn(marker, s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.warn(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.warn(marker, s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }

    @Override
    public void warn(String s, Object o) {
        logger.warn(s, o);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        logger.warn(s, o, o1);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2) {
        logger.warn(s, o, o1, o2);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3) {
        logger.warn(s, o, o1, o2, o3);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        logger.warn(s, o, o1, o2, o3, o4);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        logger.warn(s, o, o1, o2, o3, o4, o5);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        logger.warn(s, o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        logger.warn(s, o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        logger.warn(s, o, o1, o2, o3, o4, o5, o6, o7, o8);
    }

    @Override
    public void warn(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        logger.warn(s, o, o1, o2, o3, o4, o5, o6, o7, o8, o9);
    }
}
