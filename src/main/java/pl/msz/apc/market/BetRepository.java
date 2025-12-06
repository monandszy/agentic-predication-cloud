package pl.msz.apc.market;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BetRepository extends JpaRepository<Bet, UUID> {
    java.util.List<Bet> findByQuestionAndRound(Question question, int round);
}
