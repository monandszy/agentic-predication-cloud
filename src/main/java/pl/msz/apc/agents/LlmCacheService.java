package pl.msz.apc.agents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmCacheService {

    private final LlmCacheRepository llmCacheRepository;

    @Transactional(readOnly = true)
    public Optional<String> getCachedResponse(String prompt, String modelName) {
        String hash = calculateHash(prompt, modelName);
        return llmCacheRepository.findById(hash)
                .map(LlmCache::getResponseText);
    }

    @Transactional
    public void cacheResponse(String prompt, String modelName, String response) {
        String hash = calculateHash(prompt, modelName);
        if (llmCacheRepository.existsById(hash)) {
            return;
        }

        LlmCache cache = new LlmCache(
                hash,
                prompt,
                response,
                modelName,
                LocalDateTime.now()
        );
        llmCacheRepository.save(cache);
        log.debug("Cached response for hash: {}", hash);
    }

    @Transactional
    public void clearCache() {
        log.info("Clearing all LLM cache entries");
        llmCacheRepository.deleteAll();
    }

    private String calculateHash(String prompt, String modelName) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = modelName + ":" + prompt;
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
