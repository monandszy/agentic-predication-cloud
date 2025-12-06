package pl.msz.apc.ingestion;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Service
class FileLoader {

    public List<Document> loadDocuments(String directoryPath) {
        List<Document> documents = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);

        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.walk(startPath)) {
            stream.filter(p -> !Files.isDirectory(p))
                  .forEach(path -> {
                      if (path.toString().toLowerCase().endsWith(".pdf")) {
                          documents.addAll(loadPdf(path));
                      } else if (path.toString().toLowerCase().endsWith(".txt")) {
                          documents.add(loadText(path));
                      }
                  });
        } catch (IOException e) {
            throw new RuntimeException("Failed to read documents from " + directoryPath, e);
        }

        return documents;
    }

    private List<Document> loadPdf(Path path) {
        Resource resource = new FileSystemResource(path);
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageBottomMargin(0)
                        .build());

        return pdfReader.get().stream()
                .map(doc -> new Document(
                        UUID.randomUUID().toString(),
                        doc.getContent(),
                        doc.getMetadata()))
                .toList();
    }

    private Document loadText(Path path) {
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            return new Document(UUID.randomUUID().toString(), content, Map.of("source", path.getFileName().toString()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read text file " + path, e);
        }
    }
}
