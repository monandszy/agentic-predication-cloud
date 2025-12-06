package pl.msz.apc.agents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScenarioGenerationService {

    private final LlmClient llmClient;

    public List<AgentScenario> generateScenarios(List<String> facts, List<Persona> personas) {
        List<AgentScenario> scenarios = new ArrayList<>();
        String factsText = String.join("\n", facts);

        for (Persona persona : personas) {
            if (persona == Persona.FACT_EXTRACTOR) continue; // Skip utility personas
            
            log.info("Generating scenario for persona: {}", persona.getRoleName());
            String prompt = createPrompt(factsText);
            String response = llmClient.chat(prompt, persona, ModelType.SMART);
            scenarios.add(new AgentScenario(persona, response));
        }
        return scenarios;
    }

    private String createPrompt(String facts) {
        return "Based on the following facts, describe a possible future scenario for Atlantis.\n" +
               "Focus on the implications relevant to your expertise.\n" +
               "Consider both short-term (12 months) and long-term (36 months) impacts.\n" +
               "Highlight potential risks and opportunities.\n" +
               "You can reference facts by their number (e.g., [Fact 1]).\n" +
               "Do not use Markdown enumeration (like 1., -, *) for lists. Use plain text formatting.\n\n" +
               "Facts:\n" + facts + "\n\n" +
               "Scenario Description:";
    }
}
