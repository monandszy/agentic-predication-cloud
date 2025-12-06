package pl.msz.apc.agents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LlmCacheServiceTest {

    @Mock
    private LlmCacheRepository llmCacheRepository;

    @InjectMocks
    private LlmCacheService llmCacheService;

    @Test
    void shouldReturnCachedResponseIfPresent() {
        // Given
        String prompt = "Hello";
        String model = "gpt-4";
        String expectedResponse = "Hi there";
        
        LlmCache cache = new LlmCache();
        cache.setResponseText(expectedResponse);

        when(llmCacheRepository.findById(anyString())).thenReturn(Optional.of(cache));

        // When
        Optional<String> result = llmCacheService.getCachedResponse(prompt, model);

        // Then
        assertThat(result).isPresent().contains(expectedResponse);
        verify(llmCacheRepository).findById(anyString());
    }

    @Test
    void shouldReturnEmptyIfCacheMiss() {
        // Given
        when(llmCacheRepository.findById(anyString())).thenReturn(Optional.empty());

        // When
        Optional<String> result = llmCacheService.getCachedResponse("New Prompt", "gpt-4");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveToCache() {
        // Given
        String prompt = "Why is the sky blue?";
        String model = "gpt-4";
        String response = "Rayleigh scattering";

        when(llmCacheRepository.existsById(anyString())).thenReturn(false);

        // When
        llmCacheService.cacheResponse(prompt, model, response);

        // Then
        verify(llmCacheRepository).save(any(LlmCache.class));
    }
}
