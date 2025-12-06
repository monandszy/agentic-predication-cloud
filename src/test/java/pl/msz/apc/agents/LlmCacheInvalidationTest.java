package pl.msz.apc.agents;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LlmCacheInvalidationTest {

    @Autowired
    private LlmCacheService llmCacheService;

    @Autowired
    private LlmCacheRepository llmCacheRepository;

    @Test
    void shouldInvalidateCache() {
        // Given
        String prompt = "test prompt";
        String model = "test-model";
        String response = "test response";
        
        llmCacheService.cacheResponse(prompt, model, response);
        assertThat(llmCacheRepository.count()).isGreaterThan(0);

        // When
        llmCacheService.clearCache();

        // Then
        assertThat(llmCacheRepository.count()).isZero();
    }
}
