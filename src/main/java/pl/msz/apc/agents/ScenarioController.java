package pl.msz.apc.agents;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.msz.apc.reporting.PredictionScenario;
import pl.msz.apc.reporting.ScenarioSynthesizer;
import pl.msz.apc.research.FactProcessor;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/scenarios")
@RequiredArgsConstructor
@Tag(name = "Strategic Scenarios", description = "Endpoints for generating strategic reports")
public class ScenarioController {

    private final FactProcessor factProcessor;
    private final ScenarioGenerationService scenarioGenerationService;
    private final ScenarioSynthesizer scenarioSynthesizer;

    @PostMapping("/strategic")
    @Operation(summary = "Generate Strategic Report", description = "Generates a strategic report based on the provided text content.")
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
        return generateReportContent(content, focus);
    }

    @PostMapping("/strategic/file")
    @Operation(summary = "Generate Strategic Report File", description = "Generates a strategic report file based on the provided text content.")
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
    public ResponseEntity<byte[]> generateStrategicReportFile(
            @RequestBody String content,
            @RequestParam(defaultValue = "interes państwa Atlantis") String focus) {
        String report = generateReportContent(content, focus);
        byte[] fileContent = report.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=strategic_report.md")
                .contentType(MediaType.parseMediaType("text/markdown"))
                .body(fileContent);
    }

    private String generateReportContent(String content, String focus) {
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
        String executiveSummary = scenarioSynthesizer.generateExecutiveSummary(facts, agentScenarios, focus);
        log.info("Synthesized {} final scenarios and executive summary", finalScenarios.size());

        // 4. Format Output
        return formatReport(finalScenarios, facts, focus, executiveSummary);
    }

    private String formatReport(List<PredictionScenario> scenarios, List<String> facts, String focus, String executiveSummary) {
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("# FINALNY RAPORT STRATEGICZNY: ").append(focus.toUpperCase()).append("\n\n");
        
        reportContent.append("## PODSUMOWANIE WYKONAWCZE\n\n");
        reportContent.append(executiveSummary).append("\n\n");
        
        reportContent.append("## SPIS TREŚCI\n\n");
        for (PredictionScenario scenario : scenarios) {
             String header = String.format("SCENARIUSZ: %s - %s", scenario.timeframe(), scenario.variant().toUpperCase());
             reportContent.append("- **").append(header).append("**: ").append(scenario.title()).append("\n");
        }
        reportContent.append("- **Lista Faktów**\n");
        reportContent.append("\n---\n\n");

        for (PredictionScenario scenario : scenarios) {
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
                scenario.recommendations()
            ));
        }

        reportContent.append("\n## Lista Faktów\n");
        for (int i = 0; i < facts.size(); i++) {
            reportContent.append(i + 1).append(". ").append(facts.get(i)).append("\n");
        }
        
        return reportContent.toString();
    }
}
