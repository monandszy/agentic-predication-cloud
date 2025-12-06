package pl.msz.apc.reporting;

import org.springframework.stereotype.Component;
import pl.msz.apc.market.Bet;
import pl.msz.apc.market.Market;
import pl.msz.apc.market.Question;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MarkdownReportExporter implements ReportExporter {

    @Override
    public byte[] export(Market market, List<Bet> bets, String narrative) {
        StringBuilder sb = new StringBuilder();

        sb.append("# Market Report: ").append(market.getName()).append("\n\n");
        sb.append("## Description\n");
        sb.append(market.getDescription()).append("\n\n");

        sb.append("## Questions\n");
        for (Question q : market.getQuestions()) {
            sb.append("- ").append(q.getText()).append("\n");
        }
        sb.append("\n");
        
        for (Question question : market.getQuestions()) {
            sb.append("## Question: ").append(question.getText()).append("\n\n");
            
            Map<Integer, List<Bet>> betsByRound = bets.stream()
                    .filter(b -> b.getQuestion().getId().equals(question.getId()))
                    .collect(Collectors.groupingBy(Bet::getRound));
            
            betsByRound.forEach((round, roundBets) -> {
                sb.append("### Round ").append(round).append("\n");
                for (Bet bet : roundBets) {
                    sb.append("- **").append(bet.getAgentPersona().getRoleName()).append("**")
                      .append(": ").append(String.format(Locale.US, "%.2f", bet.getProbability()))
                      .append("\n  - *Rationale*: ").append(bet.getRationale()).append("\n");
                }
                sb.append("\n");
            });
        }

        sb.append("## Final Narrative\n\n");
        sb.append(narrative).append("\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getContentType() {
        return "text/markdown";
    }

    @Override
    public String getFileExtension() {
        return "md";
    }
}
