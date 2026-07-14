package org.atcraftmc.starlight.ai.chat;

public record ChatRequest(String systemPrompt, String userInput,String username,String contextId) {
}
