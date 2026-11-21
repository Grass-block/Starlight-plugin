package org.atcraftmc.starlight.data.recording;

public class RecordEntry {
    private String id;
    private final String[] headers;
    private final RecordService holder;

    public RecordEntry(String[] headers, RecordService holder) {
        this.headers = headers;
        this.holder = holder;
    }

    public RecordService getHolder() {
        return holder;
    }

    public String[] getHeaders() {
        return headers;
    }

    public String getId() {
        return id;
    }

    public void write(Object... items) {
        this.holder.write(this, items);
    }
}
