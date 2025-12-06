# Implementation Plan

## Phase 1: Foundation & Infrastructure (High Priority)
*Goal: Establish the runtime environment and project structure.*
- [x] **P1.1**: Initialize Spring Boot project with Gradle. (Req: 11)
- [x] **P1.2**: Configure Docker and Docker Compose for App and Postgres. (Req: 10)
- [x] **P1.3**: Configure PostgreSQL with `pgvector` extension. (Req: 2, 10)
- [x] **P1.4**: Set up basic module structure (`ingestion`, `agents`, `market`, `reporting`). (Req: 11)

## Phase 2: Ingestion Module (High Priority)
*Goal: Enable the system to read and understand documents.*
- [ ] **P2.1**: Implement `DocumentService` to load text files. (Req: 1)
- [ ] **P2.2**: Configure Spring AI with Gemini Embedding Model. (Req: 2)
- [ ] **P2.3**: Implement `VectorStore` integration (Postgres) for storing embeddings. (Req: 2)
- [ ] **P2.4**: Create `KnowledgeRetrievalService` to query the vector store. (Req: 2)

## Phase 3: Agents Module (High Priority)
*Goal: Create the AI actors.*
- [ ] **P3.1**: Configure Spring AI Chat Client (Gemini). (Req: 4)
- [ ] **P3.2**: Define `Agent` interface and `Persona` enum/configuration. (Req: 3)
- [ ] **P3.3**: Implement `AgentFactory` to create agents with specific System Prompts. (Req: 3)
- [ ] **P3.4**: Implement RAG injection into agent prompts using `KnowledgeRetrievalService`. (Req: 4, 6)

## Phase 4: Market Module (Medium Priority)
*Goal: Implement the core simulation logic.*
- [ ] **P4.1**: Define Domain Entities (`PredictionMarket`, `MarketQuestion`, `AgentBet`). (Req: 5, 6)
- [ ] **P4.2**: Implement "Market Maker" logic to generate questions from context. (Req: 5)
- [ ] **P4.3**: Implement "Betting Floor" logic (Round 1) - collecting bets. (Req: 6)
- [ ] **P4.4**: Implement "Debate" logic (Round 2) - sharing rationales and updating bets. (Req: 7)

## Phase 5: Reporting Module (Medium Priority)
*Goal: Produce the final output.*
- [ ] **P5.1**: Implement `ConsensusEngine` to calculate weighted averages. (Req: 8)
- [ ] **P5.2**: Implement `ReportGenerator` to synthesize rationales into a narrative. (Req: 9)
- [ ] **P5.3**: Create API/CLI entry point to trigger the full process. (Req: 9)

## Phase 6: Refinement & Testing (Low Priority)
*Goal: Polish and verify.*
- [ ] **P6.1**: Add integration tests for the full flow. (Req: 10, 11)
- [ ] **P6.2**: Tune prompts for better persona distinctiveness. (Req: 3)
