# Project Assessment & Roadmap

## Alignment with Requirements

The project **Agent Prediction Cloud (APC)** is currently **highly aligned** with the initial specifications defined in `ai/requirements.md`.

### 1. Data Ingestion & Knowledge Base (100% Aligned)
- [x] **Document Loading:** `FileLoader` correctly handles `.pdf`, `.txt`, and `.md` files.
- [x] **Vector Storage:** `VectorStoreService` integrates with PostgreSQL `pgvector` for RAG.

### 2. Agent Management (100% Aligned)
- [x] **Personas:** The `Persona` enum includes 8 distinct roles (Economist, Skeptic, Strategist, Futurist, Social Observer, Patriot, Market Maker, Reporter), exceeding the minimum of 5.
- [x] **LLM Integration:** `OpenAiLlmClient` provides a robust interface to Gemini/OpenAI models.
- [x] **Caching:** `LlmCacheService` is present to optimize API usage.

### 3. Market Mechanism (100% Aligned)
- [x] **Market Maker:** `MarketMakerService` successfully generates binary questions from context.
- [x] **Betting Floor:** `BettingService` collects confidence scores and rationales.
- [x] **Debate:** `DebateService` implements a multi-round consensus mechanism.

### 4. Reporting & Synthesis (100% Aligned)
- [x] **Aggregation:** `ConsensusCalculator` computes weighted probabilities.
- [x] **Reporting:** `MarkdownReportExporter` produces a structured, readable report with a "Final Verdict" executive summary.

### 5. Infrastructure (100% Aligned)
- [x] **Deployment:** Docker Compose configuration is in place.
- [x] **Architecture:** The project follows a clean Modular Monolith structure (`ingestion`, `agents`, `market`, `reporting`).

---

## Future Improvements (TODO)

### Phase 1: Advanced Agentic Behaviors
- [ ] **Dynamic Debate Rounds:** Instead of a fixed 2-round system, implement a convergence check. If agents are still highly divergent (>40% spread), trigger a 3rd round.
- [ ] **Persona-Specific Parameters:** Allow configuration of LLM settings per persona (e.g., `The Futurist` gets `temperature=0.9` for creativity, `The Skeptic` gets `temperature=0.2` for precision).
- [ ] **Adversarial Agent:** Explicitly program one agent to always take the contrarian view to prevent "groupthink".
- [ ] **Automatic Agent Role Creation:** Dynamically generate agent personas based on the input topic (e.g., for a medical topic, automatically create "The Doctor" and "The Bioethicist").

### Phase 2: System Robustness & Caching
- [ ] **Persistent Caching:** Ensure `LlmCacheService` uses a robust database entity with SHA-256 hashing for prompt keys and TTL (Time To Live) for cache expiration.
- [ ] **Error Recovery:** Implement circuit breakers for LLM API calls to handle prolonged outages gracefully.
- [ ] **Question Scheduling:** Implement a scheduler to generate and process multiple questions in batches or over time.

### Phase 3: User Interface & API
- [ ] **REST API:** Expose the simulation triggers via a proper Spring Boot `RestController` (currently driven by `AtlantisScenarioTest`).
- [ ] **Web Dashboard:** Create a simple frontend (React/Vue) to upload documents, start simulations, and view live progress of the debate.
- [ ] **Domain Scoping:** Allow users to input/select a specific domain via the UI to filter which input data is referenced (e.g., "Only reference documents from 2024").

### Phase 4: Analytics
- [ ] **Accuracy Tracking:** If used for real events, implement a way to resolve markets and track agent accuracy over time (Brier Score).
- [ ] **Bias Detection:** Analyze agent rationales to detect if a specific model/persona is consistently biased towards positive or negative outcomes.

### Phase 5: Advanced RAG & Reasoning
- [ ] **Fact Extraction:** Implement a pre-processing step to extract concrete, atomic facts from input documents that agents can reference explicitly.
- [ ] **Source Citations:** Enforce data references to original data in agent rationales (e.g., "[Source: Report A, Page 5]").
