package pl.msz.apc.ingestion;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final VectorStore vectorStore;
    private final TokenSplitter tokenSplitter;

    @Transactional
    public void saveDocuments(List<Document> documents) {
        for (Document doc : documents) {
            log.info("Processing document: {}", doc.id());
            var splitDocs = tokenSplitter.split(doc);
            
            var aiDocs = splitDocs.stream()
                .map(d -> new org.springframework.ai.document.Document(d.id(), d.content(), d.metadata()))
                .toList();
                
            vectorStore.add(aiDocs);
            log.info("Saved {} chunks for document {}", aiDocs.size(), doc.id());
        }
    }
}
