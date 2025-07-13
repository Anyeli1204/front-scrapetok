package com.example.gpt_model;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
@RestController
@RequestMapping("/ia")
@CrossOrigin(origins = "*")
public class ChatController {

    private ChatCompletionsClient client;
    private final String model = "gpt-4.1";

    @Value("${github.token}")
    private String githubToken;

    public ChatController() {
    }

    @PostConstruct
    public void initClient() {
        String endpoint = "https://models.inference.ai.azure.com";

        this.client = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(githubToken))
                .endpoint(endpoint)
                .buildClient();
    }

    @PostMapping("/chat/idea3")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        try {
            String userMessage = request.get("message");

            if (userMessage == null || userMessage.trim().isEmpty()) {
                return Map.of("error", "El mensaje no puede estar vacío");
            }

            List<ChatRequestMessage> chatMessages = Arrays.asList(
                    new ChatRequestSystemMessage("Eres un asistente que me ayuda a personalizar " +
                            "mi contenido de tiktok antes de publicarlo para que mi publicacion sea la mejor, " +
                            "yo te doy hashtags y tú en base de ello, me dices que titulo poner en mi video, que hashtags usar " +
                            "que sonido se adecuaria a ese hashtag o tema y que tips me darias para mi video, quiero que la informacion me la pases como un formato JSON y al final me des tu recomendacion. En este formato de json" +
                            "{titulo, descripción, hashtags, sonidos sugerido, recomendación de parte tuya} dame varias opciones"),
                    new ChatRequestUserMessage(userMessage)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
            options.setModel(model);

            ChatCompletions completions = client.complete(options);
            String response = completions.getChoices().get(0).getMessage().getContent();

            return Map.of("response", response);

        } catch (Exception e) {
            return Map.of("error", "Error al procesar la solicitud: " + e.getMessage());
        }
    }
}


