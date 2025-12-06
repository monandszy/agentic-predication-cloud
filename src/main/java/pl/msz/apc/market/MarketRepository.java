package pl.msz.apc.market;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MarketRepository extends JpaRepository<Market, UUID> {
}
