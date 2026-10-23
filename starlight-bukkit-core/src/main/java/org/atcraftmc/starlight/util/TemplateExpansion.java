package org.atcraftmc.starlight.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class TemplateExpansion {
    private final String[] prefillValue;
    private final String[] map;

    public TemplateExpansion(String[] map, String[] prefillValue) {
        this.map = map;
        this.prefillValue = prefillValue;
    }

    public static builder builder() {
        return new builder();
    }

    public static TemplateExpansion build(Consumer<builder> context) {
        var builder = builder();
        context.accept(builder);
        return builder.build();
    }


    public String expand(String template, Object... args) {
        for (var i = 0; i < this.map.length; i++) {
            var find = "{" + this.map[i] + "}";

            if (this.prefillValue[i] != null) {
                template = template.replace(find, this.prefillValue[i]);
            } else {
                template = template.replace(find, args[i].toString());
            }
        }

        return template;
    }

    public static final class builder {
        private final List<String> cache = new ArrayList<>();
        private final List<String> prefillValues = new ArrayList<>();

        public builder replacement(String name) {
            this.cache.add(name);
            this.prefillValues.add(null);
            return this;
        }

        public builder replacement(String name, String value) {
            this.cache.add(name);
            this.prefillValues.add(value);
            return this;
        }

        public TemplateExpansion build() {
            return new TemplateExpansion(this.cache.toArray(new String[0]), this.prefillValues.toArray(new String[0]));
        }
    }
}
