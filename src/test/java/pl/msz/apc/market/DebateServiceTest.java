package pl.msz.apc.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.ModelType;
import pl.msz.apc.agents.Persona;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test", "mock"})
class DebateServiceTest {

    @Autowired
    private DebateService debateService;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @MockBean
    private LlmClient llmClient;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        betRepository.deleteAll();
        questionRepository.deleteAll();
    }

    @Test
    void runDebateRound_shouldSaveNewBets() {
        // Given
        Question question = new Question();
        question.setText("Will AI replace programmers?");
        questionRepository.save(question);

        Bet previousBet = new Bet();
        previousBet.setQuestion(question);
        previousBet.setAgentPersona(Persona.ECONOMIST);
        previousBet.setProbability(0.6);
        previousBet.setRationale("Productivity gains.");
        previousBet.setRound(1);
        betRepository.save(previousBet);

        // Mock LLM response
        String mockResponse = "Probability: 0.7\nRationale: After hearing others, I am more confident.";
        when(llmClient.chat(anyString(), any(Persona.class), any(ModelType.class)))
                .thenReturn(mockResponse);

        // When
        debateService.runDebateRound(question, List.of(previousBet));

        // Then
        List<Bet> bets = betRepository.findAll();
        // Should have 2 bets now (1 initial + 1 debate)
        assertEquals(2, bets.size());
        
        Bet debateBet = bets.stream()
                .filter(b -> b.getRound() == 2)
                .findFirst()
                .orElseThrow();
        
        assertEquals(0.7, debateBet.getProbability());
        assertEquals("After hearing others, I am more confident.", debateBet.getRationale());
        assertEquals(Persona.ECONOMIST, debateBet.getAgentPersona());
    }
}
