package pl.msz.apc.agents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("mock")
public class MockLlmClient implements LlmClient {

    @Override
    public String chat(String message, Persona persona, ModelType modelType) {
        log.info("MOCK LLM: Sending message to {} using model {}: {}", persona.getRoleName(), modelType, message);
        return String.format("[MOCK RESPONSE from %s using %s] I received your message: '%s'. This is a simulated response.", 
                persona.getRoleName(), modelType, message);
    }
}
