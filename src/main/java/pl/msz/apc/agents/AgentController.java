package pl.msz.apc.agents;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
class AgentController {

    private final LlmClient llmClient;

    @PostMapping("/chat")
    public String chat(@RequestParam String message, @RequestParam(defaultValue = "ECONOMIST") Persona persona) {
        return llmClient.chat(message, persona);
    }
}
