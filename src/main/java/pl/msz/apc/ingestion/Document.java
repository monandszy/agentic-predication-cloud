package pl.msz.apc.ingestion;

import java.util.Map;

public record Document(String id, String content, Map<String, Object> metadata) {
}
