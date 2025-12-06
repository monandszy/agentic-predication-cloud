# Technical Tasks

## Phase 1: Foundation
- [x] **T1.1**: Create `docker-compose.yml` with Postgres (pgvector) and App service. (Plan: P1.2)
- [x] **T1.2**: Configure `application.yml` for Datasource and Liquibase. (Plan: P1.2)
- [x] **T1.3**: Create Java packages: `pl.msz.apc.ingestion`, `pl.msz.apc.agents`, `pl.msz.apc.market`, `pl.msz.apc.reporting`. (Plan: P1.4)
- [x] **T1.4**: Create `SharedKernel` package for common value objects. (Plan: P1.4)

## Phase 2: Ingestion Module
- [x] **T2.1**: Add `spring-ai-postgres-store` dependency. (Plan: P2.3)
- [x] **T2.2**: Create `Document` entity/record. (Plan: P2.1)
- [x] **T2.3**: Implement `FileLoader` service to read `.txt` and `.pdf` from a directory. (Plan: P2.1)
- [x] **T2.4**: Implement `TokenSplitter` to chunk text. (Plan: P2.1)
- [x] **T2.5**: Configure `EmbeddingClient` (Vendor Agnostic). (Plan: P2.2)
- [x] **T2.6**: Implement `VectorStoreService` to save chunks + embeddings. (Plan: P2.3)
- [ ] **T2.7**: Implement `RetrievalService.findSimilar(String query)` method. (Plan: P2.4)

## Phase 3: Agents Module
- [x] **T3.1**: Add `spring-ai-gemini` dependency. (Plan: P3.1)
- [x] **T3.2**: Create `Persona` enum (ECONOMIST, SKEPTIC, etc.) with description fields. (Plan: P3.2)
- [x] **T3.3**: Create `Agent` class wrapping `ChatClient`. (Plan: P3.2)
- [ ] **T3.4**: Implement `AgentFactory.create(Persona p)` returning a configured Agent. (Plan: P3.3)
- [ ] **T3.5**: Create `PromptTemplate` for betting, including placeholders for `{context}` and `{persona_bias}`. (Plan: P3.4)

## Phase 4: Market Module
- [ ] **T4.1**: Create JPA Entities: `Market`, `Question`, `Bet`. (Plan: P4.1)
- [ ] **T4.2**: Implement `MarketMakerService` that uses `RetrievalService` to find key topics and asks LLM to generate questions. (Plan: P4.2)
- [ ] **T4.3**: Implement `BettingService.collectBets(Question q, List<Agent> agents)`. (Plan: P4.3)
- [ ] **T4.4**: Implement `DebateService.runRound2(Question q, List<Bet> previousBets)`. (Plan: P4.4)

## Phase 5: Reporting Module
- [ ] **T5.1**: Implement `ConsensusCalculator` (weighted average logic). (Plan: P5.1)
- [ ] **T5.2**: Implement `NarrativeGenerator` using LLM to summarize `List<Bet>`. (Plan: P5.2)
- [ ] **T5.3**: Create a REST Controller or CLI Runner to start the simulation. (Plan: P5.3)
