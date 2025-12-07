package pl.msz.apc.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;

@Configuration
@Profile("mock")
public class FallbackEmbeddingConfig {

    @Bean
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel fallbackEmbeddingModel() {
        return new EmbeddingModel() {
            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<Double> vector = Collections.nCopies(768, 0.0);
                List<Embedding> embeddings = request.getInstructions().stream()
                        .map(text -> new Embedding(vector, 0))
                        .toList();
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public List<Double> embed(Document document) {
                return Collections.nCopies(768, 0.0);
            }

            @Override
            public List<Double> embed(String text) {
                return Collections.nCopies(768, 0.0);
            }

            @Override
            public List<List<Double>> embed(List<String> texts) {
                return texts.stream()
                        .map(text -> Collections.nCopies(768, 0.0))
                        .toList();
            }

            @Override
            public EmbeddingResponse embedForResponse(List<String> texts) {
                List<Double> vector = Collections.nCopies(768, 0.0);
                List<Embedding> embeddings = texts.stream()
                        .map(text -> new Embedding(vector, 0))
                        .toList();
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public int dimensions() {
                return 768;
            }
        };
    }
}
