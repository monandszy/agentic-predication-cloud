package pl.msz.apc.agents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LlmCacheRepository extends JpaRepository<LlmCache, String> {
}
