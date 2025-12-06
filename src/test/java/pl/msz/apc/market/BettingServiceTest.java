package pl.msz.apc.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.ModelType;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.ingestion.RetrievalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test", "mock"})
class BettingServiceTest {

    @Autowired
    private BettingService bettingService;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @MockBean
    private LlmClient llmClient;

    @MockBean
    private RetrievalService retrievalService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        betRepository.deleteAll();
        questionRepository.deleteAll();
    }

    @Test
    void collectBets_shouldSaveBetsFromAgents() {
        // Given
        Question question = new Question();
        question.setText("Will it rain tomorrow?");
        questionRepository.save(question);
        
        when(retrievalService.retrieveContext(anyString())).thenReturn("Weather forecast says 80% chance.");
        
        // Mock LLM response
        String mockResponse = "Probability: 0.8\nRationale: The forecast is high.";
        when(llmClient.chat(anyString(), any(Persona.class), any(ModelType.class)))
                .thenReturn(mockResponse);

        // When
        bettingService.collectBets(question, List.of(Persona.ECONOMIST));

        // Then
        List<Bet> bets = betRepository.findAll();
        assertFalse(bets.isEmpty());
        Bet bet = bets.get(0);
        assertEquals(0.8, bet.getProbability());
        assertEquals("The forecast is high.", bet.getRationale());
        assertEquals(Persona.ECONOMIST, bet.getAgentPersona());
    }
}
