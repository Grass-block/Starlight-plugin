package org.atcraftmc.starlight.ai.request;

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
public final class AstroBotHandler implements AIChatRequestHandler {
    private final HttpClient client;
    private final String endpoint;
    private final String apiKey;

    public AstroBotHandler(String baseUrl, String apiKey) {
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
        this.endpoint = baseUrl.replaceAll("/+$", "") + "/api/chat/send";
        this.apiKey = apiKey;
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
                return ChatResponse.error("AstroBot error: HTTP " + response.statusCode() + " - " + response.body());
            }
            return parseResponse(response.body());
        } catch (Exception e) {
            return ChatResponse.error(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private String buildRequest(ChatRequest request) {
        var root = new JsonObject();
        if (request.systemPrompt() != null && !request.systemPrompt().isEmpty()) {
            root.addProperty("system_prompt", request.systemPrompt());
        }
        root.addProperty("message", request.userInput());
        return root.toString();
    }

    private ChatResponse parseResponse(String body) {
        try {
            var content = new StringBuilder();
            for (var event : body.split("\n\n")) {
                var dataLine = findDataLine(event);
                if (dataLine == null || dataLine.equals("[DONE]")) {
                    continue;
                }
                var json = JsonParser.parseString(dataLine).getAsJsonObject();
                if (json.has("content")) {
                    content.append(json.get("content").getAsString());
                }
                if (json.has("reply")) {
                    content.append(json.get("reply").getAsString());
                }
            }
            if (!content.isEmpty()) {
                return ChatResponse.success(content.toString());
            }
            return ChatResponse.error("no content in SSE stream");
        } catch (Exception e) {
            return ChatResponse.error("parse failed: " + e.getMessage());
        }
    }

    private static String findDataLine(String event) {
        for (var line : event.split("\n")) {
            var trimmed = line.trim();
            if (trimmed.startsWith("data:")) {
                return trimmed.substring(5).trim();
            }
        }
        return null;
    }
}
