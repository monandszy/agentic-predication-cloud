package pl.msz.apc.market;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.ModelType;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.ingestion.RetrievalService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketMakerService {

    private final RetrievalService retrievalService;
    private final LlmClient llmClient;
    private final MarketRepository marketRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public Market createMarket(String topic) {
        // 1. Retrieve context
        String context = retrievalService.retrieveContext(topic);
        if (context == null || context.isEmpty()) {
            log.warn("No context found for topic: {}", topic);
            context = "No specific context available. Rely on general knowledge.";
        }
        return createMarket(topic, context);
    }

    @Transactional
    public Market createMarket(String topic, String context) {
        log.info("Creating market for topic: {}", topic);

        // 2. Generate Questions using LLM
        String prompt = String.format(
                "Based on the following context about '%s', generate 1 binary (Yes/No) prediction market question. " +
                "The question must be clear, falsifiable, and time-bound if possible. " +
                "Return ONLY the question, without numbering or extra text.\n" +
                "IMPORTANT: The output MUST be in POLISH language.\n\nContext:\n%s",
                topic, context
        );

        String response = llmClient.chat(prompt, Persona.MARKET_MAKER, ModelType.SMART);
        
        // 3. Create Market
        Market market = new Market();
        market.setName("Market: " + topic);
        market.setDescription("Prediction market for " + topic);
        marketRepository.save(market);

        // 4. Parse and Save Questions
        List<String> questionTexts = Arrays.stream(response.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        for (String text : questionTexts) {
            // Remove numbering if present (e.g., "1. Will...")
            String cleanText = text.replaceAll("^\\d+\\.\\s*", "");
            
            Question question = new Question();
            question.setText(cleanText);
            question.setMarket(market);
            questionRepository.save(question);
            market.getQuestions().add(question);
        }

        log.info("Created market {} with {} questions", market.getId(), market.getQuestions().size());
        return market;
    }
}
