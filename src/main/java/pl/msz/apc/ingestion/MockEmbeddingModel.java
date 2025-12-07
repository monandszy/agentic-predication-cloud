package pl.msz.apc.ingestion;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Profile("mock")
@org.springframework.context.annotation.Primary
public class MockEmbeddingModel implements EmbeddingModel {

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<Embedding> embeddings = new ArrayList<>();
        for (int i = 0; i < request.getInstructions().size(); i++) {
            embeddings.add(new Embedding(generateDummyVector(), i));
        }
        return new EmbeddingResponse(embeddings);
    }

    @Override
    public List<Double> embed(String text) {
        return generateDummyVector();
    }

    @Override
    public List<Double> embed(Document document) {
        return generateDummyVector();
    }
    
    public int dimensions() {
        return 768;
    }

    private List<Double> generateDummyVector() {
        return new ArrayList<>(Collections.nCopies(768, 0.0d));
    }
}
