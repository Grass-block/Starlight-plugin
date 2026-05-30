package org.atcraftmc.starlight.security.scan;

import org.apache.commons.lang3.Validate;

import java.util.function.Consumer;

public record ScannerInstance(String id, ScannerLevel level, String type, MethodPattern invocation) {
    public static Builder builder() {
        return new Builder();
    }

    public static ScannerInstance create(String id, Consumer<Builder> builder) {
        var b = ScannerInstance.builder();

        builder.accept(b);

        return b.build(id);
    }

    public static class Builder {
        private ScannerLevel level;
        private String type;
        private String invocation;

        public Builder level(ScannerLevel level) {
            this.level = level;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder match(String invocation) {
            this.invocation = invocation;
            return this;
        }

        public ScannerInstance build(String id) {
            Validate.notNull(this.level);
            Validate.notNull(this.type);
            Validate.notNull(this.invocation);

            return new ScannerInstance(id, this.level, this.type, MethodPattern.parse(this.invocation));
        }
    }
}
