package org.atcraftmc.starlight.data.jdbc.source;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;

public class SQLMapper extends HashSet<Function<String, String>> {
    public static SQLMapper create(Consumer<SQLMapper> m) {
        var mapper = new SQLMapper();
        m.accept(mapper);
        return mapper;
    }

    public static SQLMapper single(String placeholder, String tableName) {
        return new SQLMapper().replaceSQL(placeholder, tableName);
    }

    public SQLMapper replaceSQL(String match, String replacement) {
        add((s) -> s.replace(match, replacement));
        return this;
    }

    public SQLMapper processSQL(Function<String, String> function) {
        add(function);
        return this;
    }

    public String handleSQL(String in) {
        for (var h : this) {
            in = h.apply(in);
        }

        return in;
    }
}
