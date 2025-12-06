package pl.msz.apc.reporting;

import org.springframework.stereotype.Component;
import pl.msz.apc.market.Bet;
import pl.msz.apc.market.Market;
import pl.msz.apc.market.Question;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MarkdownReportExporter implements ReportExporter {

    @Override
    public byte[] export(Market market, List<Bet> bets, String narrative, String verdict) {
        StringBuilder sb = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Title and Metadata
        sb.append("# Market Report: ").append(market.getName()).append("\n\n");
        sb.append("> **Generated:** ").append(timestamp).append("\n");
        sb.append("> **Simulation ID:** ").append(market.getId()).append("\n\n");
        sb.append("---\n\n");

        // 1. Final Verdict (TL;DR)
        sb.append("## 1. Final Verdict\n\n");
        if (!market.getQuestions().isEmpty()) {
            sb.append("> **Question:** ").append(market.getQuestions().get(0).getText()).append("\n\n");
        }
        sb.append(verdict).append("\n\n");
        sb.append("---\n\n");

        // Detailed Analysis per Question
        sb.append("## 2. Detailed Analysis\n\n");
        
        for (Question question : market.getQuestions()) {
            sb.append("### Question: ").append(question.getText()).append("\n\n");
            
            Map<Integer, List<Bet>> betsByRound = bets.stream()
                    .filter(b -> b.getQuestion().getId().equals(question.getId()))
                    .collect(Collectors.groupingBy(Bet::getRound));
            
            // Sort rounds
            betsByRound.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Integer round = entry.getKey();
                    List<Bet> roundBets = entry.getValue();
                    
                    sb.append("#### Round ").append(round).append("\n\n");
                    
                    // Summary Table for the round
                    sb.append("| Agent | Probability |\n");
                    sb.append("|-------|-------------|\n");
                    for (Bet bet : roundBets) {
                        sb.append("| ").append(bet.getAgentPersona().getRoleName())
                          .append(" | ").append(String.format(Locale.US, "%.2f", bet.getProbability())).append(" |\n");
                    }
                    sb.append("\n");

                    // Detailed Rationales
                    sb.append("**Detailed Rationales:**\n\n");
                    for (Bet bet : roundBets) {
                        sb.append("##### ").append(bet.getAgentPersona().getRoleName()).append("\n");
                        sb.append("> ").append(bet.getRationale().replace("\n", "\n> ")).append("\n\n");
                    }
                    sb.append("---\n\n");
                });
        }

        // Final Narrative
        sb.append("## 3. Executive Summary\n\n");
        sb.append(narrative).append("\n");
        
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }    @Override
    public String getContentType() {
        return "text/markdown";
    }

    @Override
    public String getFileExtension() {
        return "md";
    }
}
