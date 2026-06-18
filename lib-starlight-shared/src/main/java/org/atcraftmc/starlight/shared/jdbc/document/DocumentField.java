package org.atcraftmc.starlight.shared.jdbc.document;

import com.google.gson.JsonObject;

import java.util.UUID;

/**
 * <h3>A field bind to access dom.</h3>
 * <p>
 * We access the dom in static entries instead of directly.
 * This sounds weird, but definitely an enjoyment.
 */
public abstract class DocumentField<I> {
    protected final String name;
    protected final I defaultValue;

    public DocumentField(String name, I defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public static DocumentField<Number> number(String id, Number defaultValue) {
        return new NumberField(id, defaultValue);
    }

    public static DocumentField<String> string(String id, String defaultValue) {
        return new StringField(id, defaultValue);
    }

    public static DocumentField<Boolean> bool(String id, boolean defaultValue) {
        return new BooleanField(id, defaultValue);
    }

    public static <A> DocumentField<A> custom(String id, A defaultValue, DocumentFieldCodec<A> codec) {
        return new CustomField<>(id, defaultValue, codec);
    }


    public final boolean exist(JsonObject json) {
        return json.has(this.name);
    }

    public final I get(JsonObject json) {
        if (!exist(json)) {
            set(json, this.defaultValue);
        }

        return raw(json);
    }

    public abstract I raw(JsonObject json);

    public abstract void set(JsonObject json, I value);

    public void set(DocumentDataService service, UUID id, I data) {
        var h = service.get(id);
        set(h, data);
    }

    public void set(NamedDocumentDataService service, String id, I data) {
        var h = service.get(id);
        set(h, data);
    }

    public I get(DocumentDataService service, UUID id) {
        var h = service.get(id);
        return get(h);
    }

    public I get(NamedDocumentDataService service, String id) {
        var h = service.get(id);
        return get(h);
    }


    private static final class CustomField<I> extends DocumentField<I> {
        private final DocumentFieldCodec<I> codec;

        public CustomField(String name, I defaultValue, DocumentFieldCodec<I> codec) {
            super(name, defaultValue);
            this.codec = codec;
        }

        @Override
        public I raw(JsonObject json) {
            return this.codec.decodeJson(json.get(this.name));
        }

        @Override
        public void set(JsonObject json, I value) {
            json.add(this.name, codec.encodeJson(value));
        }
    }

    private static final class BooleanField extends DocumentField<Boolean> {
        public BooleanField(String name, Boolean defaultValue) {
            super(name, defaultValue);
        }

        @Override
        public Boolean raw(JsonObject json) {
            return json.getAsJsonPrimitive(this.name).getAsBoolean();
        }

        @Override
        public void set(JsonObject json, Boolean value) {
            json.addProperty(this.name, value);
        }
    }

    private static final class NumberField extends DocumentField<Number> {
        public NumberField(String name, Number defaultValue) {
            super(name, defaultValue);
        }

        @Override
        public Number raw(JsonObject json) {
            return json.getAsJsonPrimitive(this.name).getAsNumber();
        }

        @Override
        public void set(JsonObject json, Number value) {
            json.addProperty(this.name, value);
        }
    }

    private static final class StringField extends DocumentField<String> {
        public StringField(String name, String defaultValue) {
            super(name, defaultValue);
        }

        @Override
        public String raw(JsonObject json) {
            return json.getAsJsonPrimitive(this.name).getAsString();
        }

        @Override
        public void set(JsonObject json, String value) {
            json.addProperty(this.name, value);
        }
    }
}
