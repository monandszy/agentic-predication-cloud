package pl.msz.apc.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.msz.apc.ingestion.RetrievalService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class MarketMakerServiceTest {

    @Autowired
    private MarketMakerService marketMakerService;

    @MockBean
    private RetrievalService retrievalService;

    @Test
    @Transactional
    void createMarket_shouldCreateMarketAndQuestions() {
        String topic = "Artificial Intelligence";
        
        when(retrievalService.retrieveContext(anyString())).thenReturn("Mock context for " + topic);
        
        Market market = marketMakerService.createMarket(topic);
        
        assertThat(market).isNotNull();
        assertThat(market.getName()).contains(topic);
        assertThat(market.getQuestions()).isNotEmpty();
        
        System.out.println("Created Market: " + market.getName());
        market.getQuestions().forEach(q -> System.out.println(" - Question: " + q.getText()));
    }
}
