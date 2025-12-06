package pl.msz.apc.reporting;

import pl.msz.apc.market.Bet;
import pl.msz.apc.market.Market;

import java.util.List;

public interface ReportExporter {
    byte[] export(Market market, List<Bet> bets, String narrative, String verdict);
    String getContentType();
    String getFileExtension();
}
