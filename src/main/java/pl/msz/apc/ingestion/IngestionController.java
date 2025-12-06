package pl.msz.apc.ingestion;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ingestion")
@RequiredArgsConstructor
class IngestionController {

    private final FileLoader fileLoader;
    private final VectorStoreService vectorStoreService;

    @PostMapping("/load")
    public void loadDocuments(@RequestParam String path) {
        List<Document> documents = fileLoader.loadDocuments(path);
        vectorStoreService.saveDocuments(documents);
    }
}
