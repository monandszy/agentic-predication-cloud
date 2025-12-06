package pl.msz.apc.agents;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelType {
    FAST("gemini-2.0-flash-lite"),
    SMART("gemini-2.0-flash");

    private final String modelName;
}
