package pl.msz.apc.reporting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.ModelType;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.market.Bet;
import pl.msz.apc.market.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test", "mock"})
class NarrativeGeneratorTest {

    @Autowired
    private NarrativeGenerator narrativeGenerator;

    @MockBean
    private LlmClient llmClient;

    @Test
    void generateReport_shouldCallLlmWithCorrectPrompt() {
        // Given
        Question question = new Question();
        question.setText("Will it rain?");

        Bet bet1 = new Bet();
        bet1.setAgentPersona(Persona.ECONOMIST);
        bet1.setProbability(0.8);
        bet1.setRationale("High humidity.");

        Bet bet2 = new Bet();
        bet2.setAgentPersona(Persona.SKEPTIC);
        bet2.setProbability(0.2);
        bet2.setRationale("Sensors are broken.");

        when(llmClient.chat(anyString(), any(Persona.class), any(ModelType.class)))
                .thenReturn("The market concludes...");

        // When
        String report = narrativeGenerator.generateReport(question, List.of(bet1, bet2), 0.5);

        // Then
        assertEquals("The market concludes...", report);
        verify(llmClient).chat(anyString(), any(Persona.class), any(ModelType.class));
    }
}
