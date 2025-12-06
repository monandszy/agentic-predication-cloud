package pl.msz.apc.reporting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.msz.apc.market.Bet;

import java.util.List;

@Slf4j
@Service
public class ConsensusCalculator {

    public double calculateConsensus(List<Bet> bets) {
        if (bets == null || bets.isEmpty()) {
            return 0.5; // Default to uncertainty
        }

        double sum = 0.0;
        for (Bet bet : bets) {
            sum += bet.getProbability();
        }

        double average = sum / bets.size();
        log.info("Calculated consensus from {} bets: {}", bets.size(), average);
        return average;
    }
}
