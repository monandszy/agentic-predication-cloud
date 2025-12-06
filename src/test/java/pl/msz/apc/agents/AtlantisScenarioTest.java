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
import pl.msz.apc.reporting.MarkdownReportExporter;
import pl.msz.apc.reporting.NarrativeGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
class AtlantisScenarioTest {

    @Autowired
    private pl.msz.apc.research.FactProcessor factProcessor;

    @Autowired
    private pl.msz.apc.reporting.ScenarioSynthesizer scenarioSynthesizer;

    @Autowired
    private ScenarioGenerationService scenarioGenerationService;

    @Test
    void runAtlantisSimulation() throws IOException {
        // 1. Simulating Web Scrape (Reading local file)
        System.out.println("--- Simulating Web Scrape (Atlantis Scenarios) ---");
        String content = Files.readString(Paths.get("data/Atlantis_Scenarios.md"));
        List<String> rawData = List.of(content);

        // 2. Fact Extraction
        System.out.println("--- Extracting Facts ---");
        List<String> rawFacts = factProcessor.extractFacts(rawData);
        List<String> facts = new ArrayList<>();
        for (int i = 0; i < rawFacts.size(); i++) {
            facts.add((i + 1) + ". " + rawFacts.get(i));
        }
        
        System.out.println("Found " + facts.size() + " facts:");
        facts.forEach(System.out::println);

        // 3. Agent Scenario Generation
        System.out.println("--- Generating Agent Scenarios ---");
        List<Persona> agents = List.of(Persona.ECONOMIST, Persona.SKEPTIC, Persona.STRATEGIST, Persona.FUTURIST);
        List<AgentScenario> agentScenarios = scenarioGenerationService.generateScenarios(facts, agents);

        agentScenarios.forEach(scenario -> {
            System.out.println("\n=== " + scenario.persona().getRoleName() + " ===");
            System.out.println(scenario.description());
        });

        // 4. Report Synthesis
        System.out.println("\n--- Synthesizing Final Report (Atlantis Interests) ---");
        List<pl.msz.apc.reporting.PredictionScenario> finalScenarios = scenarioSynthesizer.synthesizeScenarios(facts, agentScenarios);

        System.out.println("\n==========================================");
        System.out.println("FINAL STRATEGIC REPORT: ATLANTIS INTERESTS");
        System.out.println("==========================================\n");

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("# FINAL STRATEGIC REPORT: ATLANTIS INTERESTS\n\n");

        for (pl.msz.apc.reporting.PredictionScenario scenario : finalScenarios) {
            String scenarioText = String.format("""
                ## SCENARIO: %s - %s
                **TITLE:** %s
                
                ### Description
                %s
                
                ### Recommendations
                %s
                
                ---
                
                """,
                scenario.timeframe(),
                scenario.variant().toUpperCase(),
                scenario.title(),
                scenario.description(),
                scenario.recommendations()
            );
            
            System.out.println(scenarioText);
            reportContent.append(scenarioText);
        }

        reportContent.append("\n## List of Facts\n");
        for (String fact : facts) {
            reportContent.append(fact).append("\n");
        }

        Files.writeString(Paths.get("Atlantis_Strategic_Report.md"), reportContent.toString());
        System.out.println("Report saved to Atlantis_Strategic_Report.md");
    }
}
