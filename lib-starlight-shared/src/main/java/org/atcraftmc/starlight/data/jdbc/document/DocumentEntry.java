package org.atcraftmc.starlight.data.jdbc.document;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class DocumentEntry {
    private final JsonObject object;
    private String rawDOM;

    public DocumentEntry(String rawDOM) {
        this.rawDOM = rawDOM;
        this.object = JsonParser.parseString(rawDOM).getAsJsonObject();
    }

    public DocumentEntry(){
        this.object = new JsonObject();
        this.rawDOM = "{}";
    }

    public String serialize() {
        return this.object.toString();
    }

    public boolean dirty(){
        var current = this.object.toString();
        if(current.equals(this.rawDOM)){
            return false;
        }

        this.rawDOM = current;
        return true;
    }

    public String getRawDOM() {
        return rawDOM;
    }

    public JsonObject getObject() {
        return object;
    }
}
