package org.atcgroup.starlight.bundle.ai.chat;

public record ChatRequest(String systemPrompt, String userInput, String username, String contextId) {
}
