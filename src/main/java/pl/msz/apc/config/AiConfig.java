package pl.msz.apc.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test & !mock")
public class AiConfig {

    @Bean
    @ConditionalOnMissingBean
    public OpenAiApi openAiApi(
            @Value("${spring.ai.openai.base-url}") String baseUrl,
            @Value("${spring.ai.openai.api-key}") String apiKey) {
        return new OpenAiApi(baseUrl, apiKey);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.ai.openai.base-url", havingValue = "https://generativelanguage.googleapis.com/v1beta/openai/")
    public EmbeddingModel geminiEmbeddingModel(OpenAiApi openAiApi) {
        return new GeminiEmbeddingModel(openAiApi);
    }
}
