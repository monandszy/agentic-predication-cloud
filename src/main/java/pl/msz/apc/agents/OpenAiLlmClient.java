package pl.msz.apc.agents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!mock")
@RequiredArgsConstructor
public class OpenAiLlmClient implements LlmClient {

    private final ChatClient.Builder chatClientBuilder;
    private final LlmCacheService llmCacheService;

    @Value("${spring.ai.openai.chat.options.temperature:0.7}")
    private Float temperature;

    @Value("${spring.ai.openai.chat.options.top-p:0.9}")
    private Float topP;

    @Value("${spring.ai.openai.chat.options.max-tokens:2000}")
    private Integer maxTokens;

    @Override
    public String chat(String message, Persona persona, ModelType modelType) {
        String fullPrompt = "System: " + persona.getSystemPrompt() + "\nUser: " + message;
        String modelName = modelType.getModelName();

        return llmCacheService.getCachedResponse(fullPrompt, modelName)
                .orElseGet(() -> executeWithRetry(message, persona, modelType, fullPrompt, modelName));
    }

    private String executeWithRetry(String message, Persona persona, ModelType modelType, String fullPrompt, String modelName) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                // Rate limiting delay
                log.info("Waiting 10s before calling LLM API to avoid rate limits...");
                Thread.sleep(10000);

                log.info("Sending message to LLM as {} using model {}: {}", persona.getRoleName(), modelType, message);

                ChatClient chatClient = chatClientBuilder
                        .defaultSystem(persona.getSystemPrompt())
                        .build();

                String response = chatClient.prompt()
                        .user(message)
                        .options(OpenAiChatOptions.builder()
                                .withModel(modelName)
                                .withTemperature(temperature)
                                .withTopP(topP)
                                .withMaxTokens(maxTokens)
                                .build())
                        .call()
                        .content();

                if (response == null) {
                    throw new RuntimeException("Received null response from LLM");
                }

                log.info("Received response from LLM: {}", response);
                llmCacheService.cacheResponse(fullPrompt, modelName, response);
                return response;

            } catch (Exception e) {
                attempt++;
                log.error("Error calling LLM API (Attempt {}/{}): {}", attempt, maxRetries, e.getMessage());

                if (attempt >= maxRetries) {
                    throw new RuntimeException("Failed to get response from LLM after " + maxRetries + " attempts", e);
                }

                try {
                    log.info("Waiting 10s before retry due to failure...");
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry delay", ie);
                }
            }
        }
        throw new RuntimeException("Unreachable code");
    }

    // Default method in interface
    // public String chat(String message, Persona persona) {
    //    return chat(message, persona, ModelType.FAST);
    // }
}
