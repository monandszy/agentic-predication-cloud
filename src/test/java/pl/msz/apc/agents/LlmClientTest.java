package pl.msz.apc.agents;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ai.retry.NonTransientAiException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LlmClientTest {

    @Autowired
    private LlmClient llmClient;

    @Test
    void testFastModel() {
        testModel(ModelType.FAST);
    }

    @Test
    void testSmartModel() {
        testModel(ModelType.SMART);
    }

    private void testModel(ModelType modelType) {
        System.out.println("Testing model: " + modelType);
        try {
            String response = llmClient.chat("Hello, are you working?", Persona.ECONOMIST, modelType);
            System.out.println("Response from " + modelType + ": " + response);
            assertThat(response).isNotBlank();
        } catch (Exception e) {
            System.err.println("Error testing model " + modelType + ": " + e.getMessage());
            if (e instanceof NonTransientAiException) {
                 System.err.println("This is a non-transient error, likely quota or model availability.");
            }
            throw e;
        }
    }
}
