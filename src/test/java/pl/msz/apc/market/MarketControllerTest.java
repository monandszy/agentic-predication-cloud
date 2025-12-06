package pl.msz.apc.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.msz.apc.reporting.ConsensusCalculator;
import pl.msz.apc.reporting.MarkdownReportExporter;
import pl.msz.apc.reporting.NarrativeGenerator;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarketController.class)
@ActiveProfiles("mock")
class MarketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarketMakerService marketMakerService;

    @MockBean
    private BettingService bettingService;

    @MockBean
    private DebateService debateService;

    @MockBean
    private ConsensusCalculator consensusCalculator;

    @MockBean
    private NarrativeGenerator narrativeGenerator;

    @MockBean
    private BetRepository betRepository;

    @MockBean
    private MarkdownReportExporter reportExporter;

    @Test
    void runSimulation_shouldReturnReport() throws Exception {
        // Given
        String topic = "Will it rain?";
        Market market = new Market();
        Question question = new Question();
        question.setText(topic);
        market.setQuestions(List.of(question));

        when(marketMakerService.createMarket(anyString())).thenReturn(market);
        when(betRepository.findByQuestionAndRound(any(Question.class), eq(1))).thenReturn(Collections.emptyList());
        when(betRepository.findByQuestionAndRound(any(Question.class), eq(2))).thenReturn(Collections.emptyList());
        when(consensusCalculator.calculateConsensus(anyList())).thenReturn(0.75);
        when(narrativeGenerator.generateReport(any(Question.class), anyList(), eq(0.75))).thenReturn("Final Report Content");

        // When & Then
        mockMvc.perform(post("/api/v1/markets/simulate")
                        .param("topic", topic))
                .andExpect(status().isOk())
                .andExpect(content().string("Final Report Content"));
    }

    @Test
    void runSimulationAndDownload_shouldReturnFile() throws Exception {
        // Given
        String topic = "Will it rain?";
        Market market = new Market();
        Question question = new Question();
        question.setText(topic);
        market.setQuestions(List.of(question));

        when(marketMakerService.createMarket(anyString())).thenReturn(market);
        when(betRepository.findByQuestionAndRound(any(Question.class), eq(1))).thenReturn(Collections.emptyList());
        when(betRepository.findByQuestionAndRound(any(Question.class), eq(2))).thenReturn(Collections.emptyList());
        when(consensusCalculator.calculateConsensus(anyList())).thenReturn(0.75);
        when(narrativeGenerator.generateReport(any(Question.class), anyList(), eq(0.75))).thenReturn("Final Report Content");
        when(narrativeGenerator.generateVerdict(any(Question.class), anyList(), eq(0.75))).thenReturn("Final Verdict Content");
        
        byte[] fileContent = "Markdown Content".getBytes();
        when(reportExporter.export(any(Market.class), anyList(), anyString(), anyString())).thenReturn(fileContent);
        when(reportExporter.getContentType()).thenReturn("text/markdown");

        // When & Then
        mockMvc.perform(post("/api/v1/markets/simulate/file")
                        .param("topic", topic))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent))
                .andExpect(header().string("Content-Disposition", "attachment; filename=report.md"))
                .andExpect(content().contentType(MediaType.parseMediaType("text/markdown")));
    }
}
