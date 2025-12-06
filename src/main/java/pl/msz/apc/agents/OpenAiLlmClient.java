package pl.msz.apc.agents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!mock")
@RequiredArgsConstructor
public class OpenAiLlmClient implements LlmClient {

    private final ChatClient.Builder chatClientBuilder;
    private final LlmCacheService llmCacheService;

    @Override
    public String chat(String message, Persona persona, ModelType modelType) {
        String fullPrompt = "System: " + persona.getSystemPrompt() + "\nUser: " + message;
        String modelName = modelType.getModelName();

        return llmCacheService.getCachedResponse(fullPrompt, modelName)
                .orElseGet(() -> {
                    log.info("Sending message to LLM as {} using model {}: {}", persona.getRoleName(), modelType, message);

                    ChatClient chatClient = chatClientBuilder
                            .defaultSystem(persona.getSystemPrompt())
                            .build();

                    String response = chatClient.prompt()
                            .user(message)
                            .options(OpenAiChatOptions.builder()
                                    .withModel(modelName)
                                    .build())
                            .call()
                            .content();

                    log.info("Received response from LLM: {}", response);
                    llmCacheService.cacheResponse(fullPrompt, modelName, response);
                    return response;
                });
    }

    // Default method in interface
    // public String chat(String message, Persona persona) {
    //    return chat(message, persona, ModelType.FAST);
    // }
}
