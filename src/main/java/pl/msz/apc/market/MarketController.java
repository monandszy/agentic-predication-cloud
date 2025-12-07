package pl.msz.apc.market;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.reporting.ConsensusCalculator;
import pl.msz.apc.reporting.MarkdownReportExporter;
import pl.msz.apc.reporting.NarrativeGenerator;

import pl.msz.apc.agents.AgentScenario;
import pl.msz.apc.agents.ScenarioGenerationService;
import pl.msz.apc.reporting.PredictionScenario;
import pl.msz.apc.reporting.ScenarioSynthesizer;
import pl.msz.apc.research.FactProcessor;

import java.util.ArrayList;
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
    private final MarkdownReportExporter reportExporter;
    
    // Atlantis Scenario Services
    private final FactProcessor factProcessor;
    private final ScenarioGenerationService scenarioGenerationService;
    private final ScenarioSynthesizer scenarioSynthesizer;

    @PostMapping
    @Operation(summary = "Create a new market", description = "Generates questions based on a topic but does not run the simulation.")
    public Market createMarket(@RequestParam String topic) {
        return marketMakerService.createMarket(topic);
    }

    @PostMapping("/strategic")
    @Operation(summary = "Generate Strategic Report (Atlantis Scenario)", description = "Generates a strategic report based on the provided text content.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Raw text content containing facts and context",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain",
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            name = "Atlantis Scenario",
                            value = """
                                    # Scenariusze i Dane Testowe dla Państwa "Atlantis"
                                    Wskutek zaistniałej przed miesiącem katastrofy naturalnej wiodący światowy
                                    producent procesorów graficznych stracił 60% zdolności produkcyjnych; odbudowa
                                    
                                    mocy produkcyjnych poprzez inwestycje w filie zlokalizowane na obszarach
                                    nieobjętych katastrofą potrwa do końca roku 2028
                                    
                                    Przemysł motoryzacyjny w Europie (piątka głównych partnerów handlowy państwa
                                    Atlantis to kraje europejskie) bardzo wolno przestawia się na produkcję samochodów
                                    elektrycznych; rynek europejski zalewają tanie samochody elektryczne z Azji
                                    Wschodniej; europejski przemysł motoryzacyjny będzie miał w roku 2025 zyski na
                                    poziomie 30% średnich rocznych zysków z lat 2020-2024
                                    
                                    PKB krajów strefy euro w roku 2025 spadnie średnio o 1,5% w stosunku do roku
                                    2024
                                    
                                    Na wschodzie Ukrainy trwa słaby rozejm; Rosja kontroluje dwie główne elektrownie
                                    ukraińskie, które pracują na potrzeby konsumentów rosyjskich; gospodarka
                                    ukraińska rozwija się w tempie 4% PKB, głównie dzięki inwestycjom w przemysł
                                    zbrojeniowy i odbudowę infrastruktury
                                    
                                    Inwestycje amerykańskie w Ukrainie kierowane są do przemysłu wydobywczego
                                    (surowce krytyczne); roczne inwestycje UE w Ukrainie są na poziomie 3%
                                    ukraińskiego PKB i utrzymają się na takim poziomie do roku 2029
                                    
                                    Mamy gwałtowny wzrost udziału energii z OZE w miksie energetycznym krajów UE
                                    oraz Chin od początku roku 2028; w połowie roku 2023 średniej wielkości kraj
                                    południowoamerykański odkrył ogromne i łatwe do eksploatacji złoża ropy naftowej i
                                    gazu ziemnego dorównujące wielkością złożom Arabii Saudyjskiej i Kataru, co
                                    przełoży się pod koniec roku 2027 na nadpodaż tych paliw na światowe rynki; wzrost
                                    podaży energii z OZE oraz nadpodaż paliw węglowodorowych przekładają się na
                                    znaczny spadek cen ropy: do poziomu 30-35 USD za baryłkę; będzie to miało wpływ
                                    na budżet Rosji oraz (w mniejszym stopniu) innych krajów producentów ropy i paliw
                                    ropopochodnych
                                    """
                    )
            )
    )
    public String generateStrategicReport(
            @RequestBody String content,
            @RequestParam(defaultValue = "interes państwa Atlantis") String focus) {
        log.info("Received request to generate strategic report. Content length: {}, Focus: {}", content.length(), focus);

        // 1. Extract Facts
        List<String> facts = factProcessor.extractFacts(List.of(content));
        log.info("Extracted {} facts", facts.size());

        // 2. Generate Agent Scenarios
        List<Persona> agents = List.of(Persona.ECONOMIST, Persona.SKEPTIC, Persona.STRATEGIST, Persona.FUTURIST);
        List<AgentScenario> agentScenarios = scenarioGenerationService.generateScenarios(facts, agents);
        log.info("Generated {} agent scenarios", agentScenarios.size());

        // 3. Synthesize Report
        List<PredictionScenario> finalScenarios = scenarioSynthesizer.synthesizeScenarios(facts, agentScenarios, focus);
        log.info("Synthesized {} final scenarios", finalScenarios.size());

        // 4. Format Output
        return formatReport(finalScenarios, facts, focus);
    }

    private String formatReport(List<PredictionScenario> scenarios, List<String> facts, String focus) {
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("# FINALNY RAPORT STRATEGICZNY: ").append(focus.toUpperCase()).append("\n\n");

        for (PredictionScenario scenario : scenarios) {
            String formattedRecommendations = scenario.recommendations()
                    .replaceAll("Rec \\d+:", "\n* ") // Convert "Rec 1:" to bullet point
                    .trim();

            reportContent.append(String.format("""
                ## SCENARIUSZ: %s - %s
                **TYTUŁ:** %s
                
                ### Opis
                %s
                
                ### Rekomendacje
                %s
                
                ---
                
                """,
                scenario.timeframe(),
                scenario.variant().toUpperCase(),
                scenario.title(),
                scenario.description(),
                formattedRecommendations
            ));
        }

        reportContent.append("\n## Lista Faktów\n");
        for (int i = 0; i < facts.size(); i++) {
            reportContent.append(i + 1).append(". ").append(facts.get(i)).append("\n");
        }
        
        return reportContent.toString();
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

    @PostMapping("/simulate/context")
    @Operation(summary = "Run full simulation with provided context", description = "Runs the simulation using the provided text as context.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Raw text content containing facts and context",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain",
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            name = "Atlantis Scenario",
                            value = """
                                    # Scenariusze i Dane Testowe dla Państwa "Atlantis"
                                    Wskutek zaistniałej przed miesiącem katastrofy naturalnej wiodący światowy
                                    producent procesorów graficznych stracił 60% zdolności produkcyjnych; odbudowa
                                    
                                    mocy produkcyjnych poprzez inwestycje w filie zlokalizowane na obszarach
                                    nieobjętych katastrofą potrwa do końca roku 2028
                                    
                                    Przemysł motoryzacyjny w Europie (piątka głównych partnerów handlowy państwa
                                    Atlantis to kraje europejskie) bardzo wolno przestawia się na produkcję samochodów
                                    elektrycznych; rynek europejski zalewają tanie samochody elektryczne z Azji
                                    Wschodniej; europejski przemysł motoryzacyjny będzie miał w roku 2025 zyski na
                                    poziomie 30% średnich rocznych zysków z lat 2020-2024
                                    
                                    PKB krajów strefy euro w roku 2025 spadnie średnio o 1,5% w stosunku do roku
                                    2024
                                    
                                    Na wschodzie Ukrainy trwa słaby rozejm; Rosja kontroluje dwie główne elektrownie
                                    ukraińskie, które pracują na potrzeby konsumentów rosyjskich; gospodarka
                                    ukraińska rozwija się w tempie 4% PKB, głównie dzięki inwestycjom w przemysł
                                    zbrojeniowy i odbudowę infrastruktury
                                    
                                    Inwestycje amerykańskie w Ukrainie kierowane są do przemysłu wydobywczego
                                    (surowce krytyczne); roczne inwestycje UE w Ukrainie są na poziomie 3%
                                    ukraińskiego PKB i utrzymają się na takim poziomie do roku 2029
                                    
                                    Mamy gwałtowny wzrost udziału energii z OZE w miksie energetycznym krajów UE
                                    oraz Chin od początku roku 2028; w połowie roku 2023 średniej wielkości kraj
                                    południowoamerykański odkrył ogromne i łatwe do eksploatacji złoża ropy naftowej i
                                    gazu ziemnego dorównujące wielkością złożom Arabii Saudyjskiej i Kataru, co
                                    przełoży się pod koniec roku 2027 na nadpodaż tych paliw na światowe rynki; wzrost
                                    podaży energii z OZE oraz nadpodaż paliw węglowodorowych przekładają się na
                                    znaczny spadek cen ropy: do poziomu 30-35 USD za baryłkę; będzie to miało wpływ
                                    na budżet Rosji oraz (w mniejszym stopniu) innych krajów producentów ropy i paliw
                                    ropopochodnych
                                    """
                    )
            )
    )
    public String runSimulationWithContext(
            @RequestBody String context,
            @RequestParam(defaultValue = "Atlantis Future") String topic) {
        log.info("Starting simulation for topic: {} with provided context", topic);

        // 1. Create Market & Question with Context
        Market market = marketMakerService.createMarket(topic, context);
        if (market.getQuestions().isEmpty()) {
            return "Failed to generate questions.";
        }
        Question question = market.getQuestions().get(0);

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

    @PostMapping("/simulate/file")
    @Operation(summary = "Run full simulation and download report", description = "Runs the simulation and returns a Markdown file.")
    public ResponseEntity<byte[]> runSimulationAndDownload(
            @Parameter(description = "The topic to predict", example = "Will SpaceX reach Mars by 2030?")
            @RequestParam(defaultValue = "What is the future of AI?") String topic) {
        
        // Reuse logic (ideally refactor into a Service method, but keeping it simple here)
        Market market = marketMakerService.createMarket(topic);
        if (market.getQuestions().isEmpty()) {
            return ResponseEntity.badRequest().body("Failed to generate questions.".getBytes());
        }
        Question question = market.getQuestions().get(0);

        List<Persona> agents = List.of(Persona.ECONOMIST, Persona.SKEPTIC, Persona.STRATEGIST, Persona.FUTURIST);
        bettingService.collectBets(question, agents);

        List<Bet> round1Bets = betRepository.findByQuestionAndRound(question, 1);
        debateService.runDebateRound(question, round1Bets);

        List<Bet> round2Bets = betRepository.findByQuestionAndRound(question, 2);
        double consensus = consensusCalculator.calculateConsensus(round2Bets);
        String narrative = narrativeGenerator.generateReport(question, round2Bets, consensus);
        String verdict = narrativeGenerator.generateVerdict(question, round2Bets, consensus);

        // Collect all bets for the report
        List<Bet> allBets = new ArrayList<>();
        allBets.addAll(round1Bets);
        allBets.addAll(round2Bets);

        byte[] fileContent = reportExporter.export(market, allBets, narrative, verdict);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.md")
                .contentType(MediaType.parseMediaType(reportExporter.getContentType()))
                .body(fileContent);
    }
}
