package pl.msz.apc.reporting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.msz.apc.agents.LlmClient;
import pl.msz.apc.agents.ModelType;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.market.Bet;
import pl.msz.apc.market.Question;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NarrativeGenerator {

    private final LlmClient llmClient;

    public String generateReport(Question question, List<Bet> finalBets, double consensus) {
        log.info("Generating narrative report for question: {}", question.getText());

        String betsSummary = finalBets.stream()
                .map(b -> String.format("- %s: %.2f (Rationale: %s)",
                        b.getAgentPersona().getRoleName(),
                        b.getProbability(),
                        b.getRationale()))
                .collect(Collectors.joining("\n"));

        String prompt = String.format(
                "You are a professional market analyst writing a final report on a prediction market question.\n\n" +
                "Question: %s\n\n" +
                "Final Consensus Probability: %.2f\n\n" +
                "Agent Positions:\n%s\n\n" +
                "IMPORTANT: The output MUST be in POLISH language.\n" +
                "Write a concise executive summary (max 200 words) explaining the market's conclusion. " +
                "Highlight the key arguments that led to this consensus and any significant disagreements.",
                question.getText(),
                consensus,
                betsSummary
        );

        // We use the Market Maker persona or a neutral reporter persona for this.
        // Since we don't have a specific REPORTER persona, we can use MARKET_MAKER or just a generic system prompt.
        // But LlmClient requires a Persona. Let's use MARKET_MAKER.
        return llmClient.chat(prompt, Persona.MARKET_MAKER, ModelType.SMART);
    }

    public String generateVerdict(Question question, List<Bet> finalBets, double consensus) {
        log.info("Generating final verdict for question: {}", question.getText());

        String betsSummary = finalBets.stream()
                .map(b -> String.format("- %s: %.2f (Rationale: %s)",
                        b.getAgentPersona().getRoleName(),
                        b.getProbability(),
                        b.getRationale()))
                .collect(Collectors.joining("\n"));

        String prompt = String.format(
                "You are a professional market analyst.\n\n" +
                "Question: %s\n\n" +
                "Final Consensus Probability: %.2f\n\n" +
                "Agent Positions:\n%s\n\n" +
                "IMPORTANT: The output MUST be in POLISH language.\n" +
                "Write a single, concise paragraph (max 5 sentences) summarizing the final agent verdict. " +
                "Focus on the collective conclusion and the primary reason for it. " +
                "DO NOT repeat the question. DO NOT include any headers or introductory text like 'Final Verdict'. " +
                "Start directly with the summary.",
                question.getText(),
                consensus,
                betsSummary
        );

        return llmClient.chat(prompt, Persona.REPORTER, ModelType.SMART);
    }
}
