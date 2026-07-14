package org.atcraftmc.starlight.ai.request;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.ai.AIChatService;
import org.atcraftmc.starlight.ai.chat.ChatRequest;
import org.atcraftmc.starlight.ai.chat.ChatResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class AstroBotHandler implements AIChatRequestHandler {
    private final HttpClient client;
    private final String endpoint;
    private final String apiKey;

    public AstroBotHandler(String baseUrl, String apiKey) {
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
        this.endpoint = baseUrl.replaceAll("/+$", "") + "/api/v1/chat";
        this.apiKey = apiKey;
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

    @Override
    public ChatResponse chat(ChatRequest request) {
        var obj = new JsonObject();

        obj.addProperty("username", request.username());
        obj.addProperty("session_id", request.contextId());
        obj.addProperty("conversation_id", request.contextId());
        obj.addProperty("message", request.userInput());

        var body = HttpRequest.BodyPublishers.ofString(obj.toString());
        var req = HttpRequest.newBuilder()
                .uri(URI.create(this.endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("X-API-Key", this.apiKey)
                .method("POST", body)
                .build();

        try {
            var response = this.client.send(req, HttpResponse.BodyHandlers.ofString());

            for (var line : response.body().split("\n")) {
                if(!line.startsWith("data:")) {
                    continue;
                }

                if(!line.contains("complete")){
                    continue;
                }

                var json = JsonParser.parseString(line.substring(5)).getAsJsonObject();
                return new ChatResponse(json.get("data").getAsString(), true, "");
            }

            AIChatService.LOGGER.error("Cannot dispatch return: ");
            AIChatService.LOGGER.error(response.body());

            return new ChatResponse("", false, "No message found");
        } catch (IOException | InterruptedException e) {
            return new ChatResponse("[ERROR]", false, e.getMessage());
        }
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
}
