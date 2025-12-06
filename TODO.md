# Project Assessment & Roadmap

## Current Status: Strategic Scenario Generator (Pivot Completed)

The project **Agent Prediction Cloud (APC)** has successfully pivoted from a "Prediction Market" to a **"Multi-Perspective Strategic Scenario Generator"**. The system now ingests raw data, extracts weighted facts, generates narrative scenarios from multiple agent perspectives, and synthesizes a final strategic report for the state of "Atlantis".

### 1. Core Pipeline (Completed)
- [x] **Fact Extraction:** FactProcessor extracts atomic facts with importance weights from unstructured text.
- [x] **Agent Narratives:** ScenarioGenerationService generates detailed narratives for specific timeframes (12m, 36m) and variants (Positive, Negative) using distinct Personas (Economist, Skeptic, Strategist, Futurist).
- [x] **Report Synthesis:** ScenarioSynthesizer combines agent narratives into a structured "Final Strategic Report" with Relevance Scores (0-100).
- [x] **Atlantis Context:** The system is tuned to analyze the specific geopolitical and economic context of "Atlantis".

### 2. Data Ingestion & Knowledge Base (Retained)
- [x] **Document Loading:** FileLoader handles input files.
- [x] **Vector Storage:** VectorStoreService is available for RAG (though current pipeline uses direct context injection for focused scenarios).

### 3. Infrastructure (Stable)
- [x] **Deployment:** Docker Compose configuration is in place.
- [x] **Architecture:** Modular Monolith structure (ingestion, agents, 
eporting, 
esearch).
- [x] **Testing:** AtlantisScenarioTest serves as the main driver for the simulation.

---

## Future Improvements (TODO)

### Phase 1: Refinement & Configuration
- [ ] **Persona-Specific Parameters:** Allow configuration of LLM settings per persona (e.g., The Futurist gets 	emperature=0.9 for creativity, The Skeptic gets 	emperature=0.2 for precision).
- [ ] **Scenario Comparison:** Implement logic to compare generated scenarios against historical data or previous runs.

### Phase 2: User Interface & API
- [ ] **REST API:** Expose the scenario generation triggers via a proper Spring Boot RestController (currently driven by AtlantisScenarioTest).
- [ ] **Web Dashboard:** Create a simple frontend (React/Vue) to upload documents, define the "State" context (e.g., Atlantis, Poland, Corp X), and view the generated report.
- [ ] **PDF Export:** Generate a professional PDF version of the Markdown report.

### Phase 3: Advanced Analytics
- [ ] **Trend Analysis:** Track how scenarios change over time as new facts are added.
- [ ] **Relevance Tuning:** Implement a feedback loop to adjust the "Relevance Score" algorithm based on user feedback.

### Legacy / Deprecated (Market Mechanism)
*The following features are preserved in the codebase but bypassed in the current "Scenario" pipeline:*
- *Market Maker (Binary Questions)*
- *Betting Floor (Probabilities)*
- *Debate Rounds*
