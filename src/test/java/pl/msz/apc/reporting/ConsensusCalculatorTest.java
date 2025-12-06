package pl.msz.apc.reporting;

import org.junit.jupiter.api.Test;
import pl.msz.apc.market.Bet;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsensusCalculatorTest {

    private final ConsensusCalculator calculator = new ConsensusCalculator();

    @Test
    void calculateConsensus_shouldReturnAverage() {
        // Given
        Bet bet1 = new Bet();
        bet1.setProbability(0.8);

        Bet bet2 = new Bet();
        bet2.setProbability(0.2);

        // When
        double result = calculator.calculateConsensus(List.of(bet1, bet2));

        // Then
        assertEquals(0.5, result);
    }

    @Test
    void calculateConsensus_shouldHandleEmptyList() {
        // When
        double result = calculator.calculateConsensus(Collections.emptyList());

        // Then
        assertEquals(0.5, result); // Default uncertainty
    }

    @Test
    void calculateConsensus_shouldHandleNull() {
        // When
        double result = calculator.calculateConsensus(null);

        // Then
        assertEquals(0.5, result);
    }
}
