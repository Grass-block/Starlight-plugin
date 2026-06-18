package org.atcraftmc.starlight.ai.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.atcraftmc.starlight.ai.chat.ChatRequest;
import org.atcraftmc.starlight.ai.chat.ChatResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

//todo: test needed
public final class OpenAICompatibleHandler implements AIChatRequestHandler {
    private final HttpClient client;
    private final String endpoint;
    private final String apiKey;
    private final String model;
    private final int maxTokens;
    private final double temperature;

    public OpenAICompatibleHandler(String baseUrl, String apiKey, String model, int maxTokens, double temperature) {
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
        this.endpoint = baseUrl.replaceAll("/+$", "") + "/v1/chat/completions";
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        var body = buildRequest(request);
        var req = HttpRequest.newBuilder()
                .uri(URI.create(this.endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(60))
                .build();

        try {
            var response = this.client.send(req, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return ChatResponse.error("API error: HTTP " + response.statusCode() + " - " + response.body());
            }
            return parseResponse(response.body());
        } catch (Exception e) {
            return ChatResponse.error(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private String buildRequest(ChatRequest request) {
        var messages = new JsonArray();

        if (request.systemPrompt() != null && !request.systemPrompt().isEmpty()) {
            var systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", request.systemPrompt());
            messages.add(systemMessage);
        }

        var userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", request.userInput());
        messages.add(userMessage);

        var root = new JsonObject();
        root.addProperty("model", this.model);
        root.add("messages", messages);
        root.addProperty("max_tokens", this.maxTokens);
        root.addProperty("temperature", this.temperature);

        return root.toString();
    }

    private ChatResponse parseResponse(String json) {
        try {
            var root = JsonParser.parseString(json).getAsJsonObject();
            var choices = root.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) {
                return ChatResponse.error("no choices in response");
            }
            var message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
            var content = message.get("content").getAsString();
            return ChatResponse.success(content);
        } catch (Exception e) {
            return ChatResponse.error("parse failed: " + e.getMessage());
        }
    }
}
