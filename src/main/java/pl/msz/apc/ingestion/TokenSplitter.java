package pl.msz.apc.ingestion;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class TokenSplitter {

    private final TokenTextSplitter splitter;

    public TokenSplitter() {
        this.splitter = new TokenTextSplitter();
    }

    public List<Document> split(Document document) {
        var aiDoc = new org.springframework.ai.document.Document(document.content(), document.metadata());
        List<org.springframework.ai.document.Document> splitDocs = splitter.apply(List.of(aiDoc));
        
        return splitDocs.stream()
                .map(d -> new Document(
                        d.getId(),
                        d.getContent(),
                        d.getMetadata()))
                .toList();
    }
}
