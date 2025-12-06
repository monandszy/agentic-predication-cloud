package pl.msz.apc.reporting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.msz.apc.agents.AgentScenario;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.ModelType;
import pl.msz.apc.agents.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScenarioSynthesizer {

    private final LlmClient llmClient;

    public List<PredictionScenario> synthesizeScenarios(List<String> facts, List<AgentScenario> agentPerspectives) {
        log.info("Synthesizing final scenarios from {} facts and {} agent perspectives...", facts.size(), agentPerspectives.size());

        String factsText = String.join("\n", facts);
        StringBuilder agentsText = new StringBuilder();
        for (AgentScenario as : agentPerspectives) {
            agentsText.append("--- ").append(as.persona().getRoleName()).append(" ---\n")
                      .append(as.description()).append("\n\n");
        }

        List<PredictionScenario> scenarios = new ArrayList<>();
        scenarios.add(generateSingleScenario("12 months", "Positive", factsText, agentsText.toString()));
        scenarios.add(generateSingleScenario("12 months", "Negative", factsText, agentsText.toString()));
        scenarios.add(generateSingleScenario("36 months", "Positive", factsText, agentsText.toString()));
        scenarios.add(generateSingleScenario("36 months", "Negative", factsText, agentsText.toString()));

        return scenarios;
    }

    private PredictionScenario generateSingleScenario(String timeframe, String variant, String facts, String agentsView) {
        Persona reporter = Persona.REPORTER;
        
        String prompt = String.format(
            "You are an expert strategic analyst for the state of Atlantis.\n" +
            "Based on the provided Facts and Agent Perspectives, generate a detailed future scenario.\n" +
            "Timeframe: %s\n" +
            "Variant: %s (for Atlantis interests)\n" +
            "You can reference facts by their number (e.g., [Fact 1]).\n\n" +
            "Facts:\n%s\n\n" +
            "Agent Perspectives:\n%s\n\n" +
            "Output Format:\n" +
            "Title: [Scenario Title]\n" +
            "Description: [Detailed description of the scenario, correlations, and cause-effect links. Do not use Markdown lists.]\n" +
            "Recommendations: [Specific decisions to achieve/avoid this scenario. Do not use Markdown enumeration (1., -). Use 'Rec 1:', 'Rec 2:' etc.]",
            timeframe, variant, facts, agentsView
        );

        String response = llmClient.chat(prompt, reporter, ModelType.SMART);
        return parseResponse(response, timeframe, variant);
    }

    private PredictionScenario parseResponse(String response, String timeframe, String variant) {
        String title = "Unknown Title";
        String description = "";
        String recommendations = "";

        Pattern titlePattern = Pattern.compile("Title:\\s*(.+)");
        
        Matcher mTitle = titlePattern.matcher(response);
        if (mTitle.find()) title = mTitle.group(1).trim();

        // Simple parsing for description and recommendations (assuming they follow the headers)
        String[] parts = response.split("Recommendations:");
        if (parts.length > 0) {
            String descPart = parts[0];
            if (descPart.contains("Description:")) {
                description = descPart.split("Description:")[1].trim();
            } else {
                description = descPart; // Fallback
            }
        }
        if (parts.length > 1) {
            recommendations = parts[1].trim();
        }

        return new PredictionScenario(title, timeframe, variant, description, recommendations);
    }
}
