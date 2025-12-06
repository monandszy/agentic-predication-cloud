package pl.msz.apc.ingestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final VectorStore vectorStore;

    public String retrieveContext(String query) {
        log.info("Retrieving context for query: {}", query);
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(5)
        );

        String context = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));
        
        log.info("Retrieved {} documents", similarDocuments.size());
        return context;
    }
}
