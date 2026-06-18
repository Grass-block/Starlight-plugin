package org.atcraftmc.starlight.ai.chat;

public final class ChatResponse {
    private final String content;
    private final boolean success;
    private final String error;

    public ChatResponse(String content, boolean success, String error) {
        this.content = content;
        this.success = success;
        this.error = error;
    }

    public static ChatResponse success(String content) {
        return new ChatResponse(content, true, null);
    }

    public static ChatResponse error(String error) {
        return new ChatResponse(null, false, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getContent() {
        return content;
    }

    public String getError() {
        return error;
    }
}
