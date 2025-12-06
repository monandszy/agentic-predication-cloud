package pl.msz.apc.market;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.reporting.ConsensusCalculator;
import pl.msz.apc.reporting.NarrativeGenerator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/markets")
@RequiredArgsConstructor
@Tag(name = "Market Simulation", description = "Endpoints for running prediction market simulations")
public class MarketController {

    private final MarketMakerService marketMakerService;
    private final BettingService bettingService;
    private final DebateService debateService;
    private final ConsensusCalculator consensusCalculator;
    private final NarrativeGenerator narrativeGenerator;
    private final BetRepository betRepository;

    @PostMapping
    @Operation(summary = "Create a new market", description = "Generates questions based on a topic but does not run the simulation.")
    public Market createMarket(@RequestParam String topic) {
        return marketMakerService.createMarket(topic);
    }

    @PostMapping("/simulate")
    @Operation(summary = "Run full simulation", description = "Creates a market, collects bets from agents, runs a debate round, and generates a final report.")
    @ApiResponse(responseCode = "200", description = "Simulation successful, returns narrative report")
    public String runSimulation(
            @Parameter(description = "The topic to predict", example = "Will SpaceX reach Mars by 2030?")
            @RequestParam(defaultValue = "What is the future of AI?") String topic) {
        log.info("Starting simulation for topic: {}", topic);

        // 1. Create Market & Question
        Market market = marketMakerService.createMarket(topic);
        if (market.getQuestions().isEmpty()) {
            return "Failed to generate questions.";
        }
        Question question = market.getQuestions().get(0); // Take the first one for now

        // 2. Round 1: Initial Bets
        List<Persona> agents = List.of(Persona.ECONOMIST, Persona.SKEPTIC, Persona.STRATEGIST, Persona.FUTURIST);
        bettingService.collectBets(question, agents);

        // 3. Round 2: Debate
        List<Bet> round1Bets = betRepository.findByQuestionAndRound(question, 1);
        debateService.runDebateRound(question, round1Bets);

        // 4. Consensus & Reporting
        List<Bet> round2Bets = betRepository.findByQuestionAndRound(question, 2);
        double consensus = consensusCalculator.calculateConsensus(round2Bets);
        String report = narrativeGenerator.generateReport(question, round2Bets, consensus);

        log.info("Simulation complete. Consensus: {}", consensus);
        return report;
    }
}
