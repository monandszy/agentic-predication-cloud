package pl.msz.apc.market;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.ModelType;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.ingestion.RetrievalService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BettingService {

    private final LlmClient llmClient;
    private final BetRepository betRepository;
    private final RetrievalService retrievalService;

    @Transactional
    public void collectBets(Question question, List<Persona> personas) {
        log.info("Collecting bets for question: {}", question.getText());

        // Retrieve context for the question to help agents decide
        String context = retrievalService.retrieveContext(question.getText());
        if (context == null || context.isEmpty()) {
            context = "No specific context available.";
        }

        for (Persona persona : personas) {
            if (persona == Persona.MARKET_MAKER) {
                continue; // Market Maker doesn't bet
            }
            processAgentBet(question, persona, context);
        }
    }

    private void processAgentBet(Question question, Persona persona, String context) {
        log.info("Asking {} for a bet...", persona.getRoleName());

        String prompt = String.format(
                "Question: %s\n\n" +
                "Context:\n%s\n\n" +
                "Based on your persona and the provided context, estimate the probability (0.0 to 1.0) of the answer being YES. " +
                "Provide a rationale for your decision in 1-3 clean paragraphs. Do not use markdown formatting.\n" +
                "IMPORTANT: The output MUST be in POLISH language.\n" +
                "Format your response exactly as follows:\n" +
                "Probability: [0.0-1.0]\n" +
                "Rationale: [Your explanation in Polish]",
                question.getText(), context
        );

        try {
            String response = llmClient.chat(prompt, persona, ModelType.SMART);
            Bet bet = parseBetResponse(response);
            
            bet.setQuestion(question);
            bet.setAgentPersona(persona);
            bet.setRound(1); // Initial round
            
            betRepository.save(bet);
            log.info("Saved bet from {}: Probability {}", persona.getRoleName(), bet.getProbability());

        } catch (Exception e) {
            log.error("Failed to collect bet from {}", persona.getRoleName(), e);
        }
    }

    private Bet parseBetResponse(String response) {
        Bet bet = new Bet();
        
        // Simple regex to extract probability
        Pattern probPattern = Pattern.compile("Probability:\\s*([0-9]*\\.?[0-9]+)");
        Matcher probMatcher = probPattern.matcher(response);
        
        if (probMatcher.find()) {
            try {
                double prob = Double.parseDouble(probMatcher.group(1));
                // Clamp value
                prob = Math.max(0.0, Math.min(1.0, prob));
                bet.setProbability(prob);
            } catch (NumberFormatException e) {
                log.warn("Could not parse probability, defaulting to 0.5");
                bet.setProbability(0.5);
            }
        } else {
            log.warn("Probability not found in response, defaulting to 0.5");
            bet.setProbability(0.5);
        }

        // Extract rationale (everything after "Rationale:")
        Pattern rationalePattern = Pattern.compile("Rationale:\\s*(.*)", Pattern.DOTALL);
        Matcher rationaleMatcher = rationalePattern.matcher(response);
        
        if (rationaleMatcher.find()) {
            bet.setRationale(rationaleMatcher.group(1).trim());
        } else {
            bet.setRationale(response); // Fallback to full response
        }
        
        return bet;
    }
}
