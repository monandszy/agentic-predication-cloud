package pl.msz.apc.market;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.ModelType;
import pl.msz.apc.agents.Persona;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebateService {

    private final LlmClient llmClient;
    private final BetRepository betRepository;

    @Transactional
    public void runDebateRound(Question question, List<Bet> previousBets) {
        log.info("Running debate round for question: {}", question.getText());

        String debateSummary = summarizePreviousBets(previousBets);

        for (Bet previousBet : previousBets) {
            Persona persona = previousBet.getAgentPersona();
            processAgentReconsideration(question, persona, previousBet, debateSummary);
        }
    }

    private String summarizePreviousBets(List<Bet> bets) {
        return bets.stream()
                .map(b -> String.format("- %s: Probability %.2f. Rationale: %s",
                        b.getAgentPersona().getRoleName(),
                        b.getProbability(),
                        b.getRationale()))
                .collect(Collectors.joining("\n"));
    }

    private void processAgentReconsideration(Question question, Persona persona, Bet previousBet, String debateSummary) {
        log.info("Asking {} to reconsider...", persona.getRoleName());

        String prompt = String.format(
                "Question: %s\n\n" +
                "Your previous position:\nProbability: %.2f\nRationale: %s\n\n" +
                "Here is what other agents think:\n%s\n\n" +
                "Based on these arguments, please reconsider your position. You can change your probability or keep it the same. " +
                "Provide an updated rationale in 1-3 clean paragraphs, addressing why you agree or disagree with others. Do not use markdown formatting.\n" +
                "Format your response exactly as follows:\n" +
                "Probability: [0.0-1.0]\n" +
                "Rationale: [Your explanation]",
                question.getText(),
                previousBet.getProbability(),
                previousBet.getRationale(),
                debateSummary
        );

        try {
            String response = llmClient.chat(prompt, persona, ModelType.SMART);
            Bet newBet = parseBetResponse(response);
            
            newBet.setQuestion(question);
            newBet.setAgentPersona(persona);
            newBet.setRound(previousBet.getRound() + 1);
            
            betRepository.save(newBet);
            log.info("Saved debate bet from {}: Probability {}", persona.getRoleName(), newBet.getProbability());

        } catch (Exception e) {
            log.error("Failed to collect debate bet from {}", persona.getRoleName(), e);
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
