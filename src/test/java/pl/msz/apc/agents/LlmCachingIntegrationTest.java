package pl.msz.apc.agents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.ai.openai.api-key=dummy")
class LlmCachingIntegrationTest {

    @Autowired
    private LlmClient llmClient;

    @Autowired
    private LlmCacheRepository llmCacheRepository;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient.Builder chatClientBuilder;

    @BeforeEach
    void setUp() {
        llmCacheRepository.deleteAll();
    }

    @Test
    void shouldCacheResponseAndAvoidSecondCall() {
        // Given
        String prompt = "Hello World";
        String aiResponse = "Hello Human";
        Persona persona = Persona.ECONOMIST;

        // Mocking the fluent API chain using Deep Stubs
        when(chatClientBuilder.defaultSystem(anyString())
                .build()
                .prompt()
                .user(anyString())
                .options(any())
                .call()
                .content())
                .thenReturn(aiResponse);

        // When 1: First call (Cache Miss)
        String result1 = llmClient.chat(prompt, persona);

        // Then 1
        assertThat(result1).isEqualTo(aiResponse);
        assertThat(llmCacheRepository.count()).isEqualTo(1);
        
        // Verify that the chain was called once
        verify(chatClientBuilder.defaultSystem(anyString())
                .build()
                .prompt()
                .user(anyString())
                .options(any())
                .call(), times(1)).content();

        // When 2: Second call (Cache Hit)
        String result2 = llmClient.chat(prompt, persona);

        // Then 2
        assertThat(result2).isEqualTo(aiResponse);
        
        // Verify that the chain was NOT called again (count remains 1)
        verify(chatClientBuilder.defaultSystem(anyString())
                .build()
                .prompt()
                .user(anyString())
                .options(any())
                .call(), times(1)).content();
    }

    @Test
    void shouldRetryOnFailure() {
        // Given
        String prompt = "Retry Me";
        String aiResponse = "Success after retry";
        Persona persona = Persona.SKEPTIC;

        // Mocking: First call throws, Second call succeeds
        when(chatClientBuilder.defaultSystem(anyString())
                .build()
                .prompt()
                .user(anyString())
                .options(any())
                .call()
                .content())
                .thenThrow(new RuntimeException("API Error"))
                .thenReturn(aiResponse);

        // When
        String result = llmClient.chat(prompt, persona);

        // Then
        assertThat(result).isEqualTo(aiResponse);
        assertThat(llmCacheRepository.count()).isEqualTo(1);
        
        // Verify called twice (1 failure + 1 success)
        verify(chatClientBuilder.defaultSystem(anyString())
                .build()
                .prompt()
                .user(anyString())
                .options(any())
                .call(), times(2)).content();
    }
}
