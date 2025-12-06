package pl.msz.apc.research;

import org.springframework.stereotype.Service;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.agents.ModelType;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FactProcessor {

    private final LlmClient llmClient;
    
    public List<String> extractFacts(List<String> rawData) {
        if (rawData.isEmpty()) {
            return new ArrayList<>();
        }

        // Combine raw data (truncating if necessary to fit context)
        String combinedData = String.join("\n\n", rawData);
        if (combinedData.length() > 20000) {
            combinedData = combinedData.substring(0, 20000) + "...(truncated)";
        }

        String prompt = "Extract key facts from the following text. " +
                        "If a fact has an importance weight (e.g., 'waga istotności: 30'), include it in brackets at the end.\n\n" + combinedData;
        
        String response = llmClient.chat(prompt, Persona.FACT_EXTRACTOR, ModelType.SMART);
        
        // Parse the response into a list
        List<String> facts = new ArrayList<>();
        for (String line : response.split("\n")) {
            if (line.trim().startsWith("-")) {
                facts.add(line.trim().substring(1).trim());
            }
        }
        
        return facts;
    }
}
