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

    @Override
    public String chat(String message, Persona persona, ModelType modelType) {
        log.info("Sending message to LLM as {} using model {}: {}", persona.getRoleName(), modelType, message);
        
        ChatClient chatClient = chatClientBuilder
                .defaultSystem(persona.getSystemPrompt())
                .build();

        String response = chatClient.prompt()
                .user(message)
                .options(OpenAiChatOptions.builder()
                        .withModel(modelType.getModelName())
                        .build())
                .call()
                .content();

        log.info("Received response from LLM: {}", response);
        return response;
    }

    // Default method in interface
    // public String chat(String message, Persona persona) {
    //    return chat(message, persona, ModelType.FAST);
    // }
}
