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

    public List<PredictionScenario> synthesizeScenarios(List<String> facts, List<AgentScenario> agentPerspectives, String focus) {
        log.info("Synthesizing final scenarios from {} facts and {} agent perspectives with focus: {}", facts.size(), agentPerspectives.size(), focus);

        StringBuilder factsBuilder = new StringBuilder();
        for (int i = 0; i < facts.size(); i++) {
            factsBuilder.append(i + 1).append(". ").append(facts.get(i)).append("\n");
        }
        String factsText = factsBuilder.toString();

        StringBuilder agentsText = new StringBuilder();
        for (AgentScenario as : agentPerspectives) {
            agentsText.append("--- ").append(as.persona().getRoleName()).append(" ---\n")
                      .append(as.description()).append("\n\n");
        }

        List<PredictionScenario> scenarios = new ArrayList<>();
        scenarios.add(generateSingleScenario("12 miesięcy", "Pozytywny", factsText, agentsText.toString(), focus));
        scenarios.add(generateSingleScenario("12 miesięcy", "Negatywny", factsText, agentsText.toString(), focus));
        scenarios.add(generateSingleScenario("36 miesięcy", "Pozytywny", factsText, agentsText.toString(), focus));
        scenarios.add(generateSingleScenario("36 miesięcy", "Negatywny", factsText, agentsText.toString(), focus));

        return scenarios;
    }

    public String generateExecutiveSummary(List<String> facts, List<AgentScenario> agentPerspectives, String focus) {
        StringBuilder factsBuilder = new StringBuilder();
        for (int i = 0; i < facts.size(); i++) {
            factsBuilder.append(i + 1).append(". ").append(facts.get(i)).append("\n");
        }
        String factsText = factsBuilder.toString();

        StringBuilder agentsText = new StringBuilder();
        for (AgentScenario as : agentPerspectives) {
            agentsText.append("--- ").append(as.persona().getRoleName()).append(" ---\n")
                      .append(as.description()).append("\n\n");
        }

        String prompt = String.format(
            "You are an expert strategic analyst for the state of Atlantis.\n" +
            "Based on the provided Facts and Agent Perspectives, write a concise Executive Summary (max 200 words) of the strategic situation.\n" +
            "Focus on the most critical threats and opportunities for %s.\n" +
            "Facts:\n%s\n\n" +
            "Agent Perspectives:\n%s\n\n" +
            "IMPORTANT: The output MUST be in POLISH language.\n" +
            "Do not use any headers like 'Executive Summary'. Just write the summary text.",
            focus, factsText, agentsText.toString()
        );

        return llmClient.chat(prompt, Persona.REPORTER, ModelType.SMART);
    }

    private PredictionScenario generateSingleScenario(String timeframe, String variant, String facts, String agentsView, String focus) {
        Persona reporter = Persona.REPORTER;
        
        String prompt = String.format(
            "You are an expert strategic analyst for the state of Atlantis.\n" +
            "Based on the provided Facts and Agent Perspectives, generate a detailed future scenario.\n" +
            "Timeframe: %s\n" +
            "Variant: %s (for %s)\n" +
            "You can reference facts by their number (e.g., [Fact 1]).\n\n" +
            "Facts:\n%s\n\n" +
            "Agent Perspectives:\n%s\n\n" +
            "IMPORTANT: The output MUST be in POLISH language.\n" +
            "Output Format:\n" +
            "Title: [Scenario Title in Polish]\n" +
            "Description: [Detailed description of the scenario, correlations, and cause-effect links in Polish. Do not use Markdown lists.]\n" +
            "Recommendations: [Specific decisions to achieve/avoid this scenario in Polish. Format as a Markdown bulleted list.]",
            timeframe, variant, focus, facts, agentsView
        );

        String response = llmClient.chat(prompt, reporter, ModelType.SMART);
        return parseResponse(response, timeframe, variant);
    }

    private PredictionScenario parseResponse(String response, String timeframe, String variant) {
        String title = "Unknown Title";
        String description = "";
        String recommendations = "";

        // Normalize headers to handle Markdown bolding or different casing
        String normalized = response
                .replaceAll("(?i)\\*\\*Title:?\\*\\*", "Title:")
                .replaceAll("(?i)##\\s*Title:?", "Title:")
                .replaceAll("(?i)\\*\\*Tytuł:?\\*\\*", "Title:")
                .replaceAll("(?i)##\\s*Tytuł:?", "Title:")
                .replaceAll("(?i)Tytuł:", "Title:")
                .replaceAll("(?i)\\*\\*Description:?\\*\\*", "Description:")
                .replaceAll("(?i)##\\s*Description:?", "Description:")
                .replaceAll("(?i)\\*\\*Opis:?\\*\\*", "Description:")
                .replaceAll("(?i)##\\s*Opis:?", "Description:")
                .replaceAll("(?i)Opis:", "Description:")
                .replaceAll("(?i)\\*\\*Recommendations:?\\*\\*", "Recommendations:")
                .replaceAll("(?i)##\\s*Recommendations:?", "Recommendations:")
                .replaceAll("(?i)\\*\\*Rekomendacje:?\\*\\*", "Recommendations:")
                .replaceAll("(?i)##\\s*Rekomendacje:?", "Recommendations:")
                .replaceAll("(?i)Rekomendacje:", "Recommendations:")
                .replaceAll("(?i)\\*\\*Zalecenia:?\\*\\*", "Recommendations:")
                .replaceAll("(?i)##\\s*Zalecenia:?", "Recommendations:")
                .replaceAll("(?i)Zalecenia:", "Recommendations:");

        // Extract Recommendations first to isolate the top part
        int recIndex = normalized.indexOf("Recommendations:");
        String topPart = normalized;
        if (recIndex != -1) {
            recommendations = normalized.substring(recIndex + "Recommendations:".length()).trim();
            // Clean up any remaining headers in recommendations
            recommendations = recommendations
                    .replaceAll("(?i)^\\s*Zalecenia:?\\s*", "")
                    .replaceAll("(?i)^\\s*Rekomendacje:?\\s*", "")
                    .trim();
            topPart = normalized.substring(0, recIndex);
        }

        // Now extract Title and Description from topPart
        
        // 1. Try standard "Title:" pattern
        Pattern titlePattern = Pattern.compile("Title:\\s*(.+)");
        Matcher mTitle = titlePattern.matcher(topPart);
        
        if (mTitle.find()) {
            title = mTitle.group(1).trim();
            // Description is everything after the title line
            // Check if "Description:" tag exists
            int descIndex = topPart.indexOf("Description:");
            if (descIndex != -1) {
                description = topPart.substring(descIndex + "Description:".length()).trim();
            } else {
                // Everything after title match
                description = topPart.substring(mTitle.end()).trim();
            }
        } else {
            // 2. Try "Scenario:" or "Future Scenario:" pattern at the start
            Pattern scenarioPattern = Pattern.compile("(?i)^\\s*(?:\\*\\*)?(?:Future\\s+)?Scenario:?\\s*(.+)$", Pattern.MULTILINE);
            Matcher mScenario = scenarioPattern.matcher(topPart);
            
            if (mScenario.find()) {
                title = mScenario.group(1).trim();
                // Description is everything after this line
                int descIndex = topPart.indexOf("Description:");
                if (descIndex != -1) {
                    description = topPart.substring(descIndex + "Description:".length()).trim();
                } else {
                    description = topPart.substring(mScenario.end()).trim();
                }
            } else {
                // 3. No title found. 
                // Check if "Description:" exists
                int descIndex = topPart.indexOf("Description:");
                if (descIndex != -1) {
                    description = topPart.substring(descIndex + "Description:".length()).trim();
                    // Maybe the title is before "Description:"?
                    String potentialTitle = topPart.substring(0, descIndex).trim();
                    if (!potentialTitle.isEmpty() && potentialTitle.length() < 150) {
                        title = potentialTitle;
                    }
                } else {
                    // No "Description:" tag.
                    // Assume the first line is the title if it's short, and the rest is description.
                    String[] lines = topPart.split("\n", 2);
                    if (lines.length > 0) {
                        String firstLine = lines[0].trim();
                        // Heuristic: Title shouldn't be too long and shouldn't end with a period (usually)
                        if (firstLine.length() > 0 && firstLine.length() < 150 && !firstLine.endsWith(".")) {
                             title = firstLine;
                             if (lines.length > 1) description = lines[1].trim();
                        } else {
                             description = topPart.trim();
                        }
                    }
                }
            }
        }

        // Clean up title
        if (title.startsWith("**") && title.endsWith("**")) {
            title = title.substring(2, title.length() - 2);
        } else if (title.startsWith("\"") && title.endsWith("\"")) {
            title = title.substring(1, title.length() - 1);
        }
        
        // Clean up description
        if (description.startsWith("**") && description.endsWith("**")) {
             description = description.substring(2, description.length() - 2);
        }

        return new PredictionScenario(title, timeframe, variant, description, recommendations);
    }
}
