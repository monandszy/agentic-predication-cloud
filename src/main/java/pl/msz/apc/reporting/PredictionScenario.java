package pl.msz.apc.reporting;

public record PredictionScenario(
    String title,
    String timeframe, // "12 months" or "36 months"
    String variant,   // "Positive" or "Negative"
    String description,
    String recommendations
) {}
