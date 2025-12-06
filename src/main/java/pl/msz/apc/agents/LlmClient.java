package pl.msz.apc.agents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmClient {

    private final ChatClient.Builder chatClientBuilder;

    public String chat(String message, Persona persona) {
        log.info("Sending message to LLM as {}: {}", persona.getRoleName(), message);
        
        ChatClient chatClient = chatClientBuilder
                .defaultSystem(persona.getSystemPrompt())
                .build();

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        log.info("Received response from LLM: {}", response);
        return response;
    }
}
