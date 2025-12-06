package pl.msz.apc.agents;

public interface LlmClient {
    String chat(String message, Persona persona, ModelType modelType);

    default String chat(String message, Persona persona) {
        return chat(message, persona, ModelType.FAST);
    }
}
