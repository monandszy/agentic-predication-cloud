package pl.msz.apc.agents;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "llm_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LlmCache {

    @Id
    @Column(name = "prompt_hash", length = 64)
    private String promptHash;

    @Column(name = "prompt_text", columnDefinition = "TEXT", nullable = false)
    private String promptText;

    @Column(name = "response_text", columnDefinition = "TEXT", nullable = false)
    private String responseText;

    @Column(name = "model_name", length = 50, nullable = false)
    private String modelName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
