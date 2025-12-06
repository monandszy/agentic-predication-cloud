package pl.msz.apc.agents;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.msz.apc.ingestion.FileLoader;
import pl.msz.apc.ingestion.VectorStoreService;
import pl.msz.apc.market.*;
import pl.msz.apc.reporting.ConsensusCalculator;
import pl.msz.apc.reporting.NarrativeGenerator;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
class AtlantisScenarioTest {

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private VectorStoreService vectorStoreService;

    @Autowired
    private MarketMakerService marketMakerService;

    @Autowired
    private BettingService bettingService;

    @Autowired
    private DebateService debateService;

    @Autowired
    private ConsensusCalculator consensusCalculator;

    @Autowired
    private NarrativeGenerator narrativeGenerator;

    @Autowired
    private BetRepository betRepository;

    // @DynamicPropertySource
    // static void configureProperties(DynamicPropertyRegistry registry) {
    //     // Override datasource to localhost for the test execution
    //     registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/postgres");
    // }

    @Test
    void runAtlantisSimulation() {
        // 1. Ingest Data
        System.out.println("--- Ingesting Atlantis Scenarios ---");
        var documents = fileLoader.loadDocuments("data");
        vectorStoreService.saveDocuments(documents);
        System.out.println("--- Ingestion Complete ---");

        // 2. Create Market
        String topic = "Atlantis Future Scenarios";
        System.out.println("--- Creating Market for: " + topic + " ---");
        Market market = marketMakerService.createMarket(topic);
        
        if (market.getQuestions().isEmpty()) {
            throw new RuntimeException("No questions generated for topic: " + topic);
        }

        Question question = market.getQuestions().get(0);
        System.out.println("Generated Question: " + question.getText());

        // 3. Round 1: Initial Bets
        System.out.println("--- Round 1: Collecting Bets ---");
        List<Persona> agents = List.of(Persona.ECONOMIST, Persona.SKEPTIC, Persona.STRATEGIST, Persona.FUTURIST);
        bettingService.collectBets(question, agents);

        // 4. Round 2: Debate
        System.out.println("--- Round 2: Debate ---");
        List<Bet> round1Bets = betRepository.findByQuestionAndRound(question, 1);
        debateService.runDebateRound(question, round1Bets);

        // 5. Consensus & Reporting
        System.out.println("--- Generating Report ---");
        List<Bet> round2Bets = betRepository.findByQuestionAndRound(question, 2);
        double consensus = consensusCalculator.calculateConsensus(round2Bets);
        String report = narrativeGenerator.generateReport(question, round2Bets, consensus);

        System.out.println("\n==========================================");
        System.out.println("FINAL REPORT");
        System.out.println("==========================================\n");
        System.out.println(report);
        System.out.println("\n==========================================");
    }
}
