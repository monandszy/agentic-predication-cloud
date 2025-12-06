package pl.msz.apc.reporting;

import org.junit.jupiter.api.Test;
import pl.msz.apc.agents.Persona;
import pl.msz.apc.market.Bet;
import pl.msz.apc.market.Market;
import pl.msz.apc.market.Question;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownReportExporterTest {

    private final MarkdownReportExporter exporter = new MarkdownReportExporter();

    @Test
    void shouldGenerateMarkdownReport() {
        // Given
        Market market = new Market();
        market.setName("Test Market");
        market.setDescription("A test market description");
        
        Question question = new Question();
        question.setId(UUID.randomUUID());
        question.setText("Will this test pass?");
        market.setQuestions(List.of(question));

        Bet bet1 = new Bet();
        bet1.setQuestion(question);
        bet1.setRound(1);
        bet1.setAgentPersona(Persona.ECONOMIST);
        bet1.setProbability(0.85);
        bet1.setRationale("Economic indicators look good.");

        Bet bet2 = new Bet();
        bet2.setQuestion(question);
        bet2.setRound(2);
        bet2.setAgentPersona(Persona.SKEPTIC);
        bet2.setProbability(0.40);
        bet2.setRationale("Too much uncertainty.");

        List<Bet> bets = List.of(bet1, bet2);
        String narrative = "The market concluded with mixed feelings.";
        String verdict = "The final verdict is uncertain.";

        // When
        byte[] result = exporter.export(market, bets, narrative, verdict);
        String report = new String(result, StandardCharsets.UTF_8);

        // Then
        assertThat(report).contains("# Market Report: Test Market");
        assertThat(report).contains("## Description\nA test market description");
        assertThat(report).contains("- Will this test pass?");
        assertThat(report).contains("### Round 1");
        assertThat(report).contains("**The Economist**: 0.85");
        assertThat(report).contains("- *Rationale*: Economic indicators look good.");
        assertThat(report).contains("### Round 2");
        assertThat(report).contains("**The Skeptic**: 0.40");
        assertThat(report).contains("## Final Narrative");
        assertThat(report).contains("The market concluded with mixed feelings.");
    }
}
