# Technical Specification: Agent Prediction Cloud (APC)

**Version:** 0.2 (Refined Architecture)
**Architecture:** Modular Monolith
**Stack:** Java 21, Spring Boot 3.3, PostgreSQL, Spring AI (Vendor Agnostic)

---

## 1. Architectural Overview

The system follows a **Modular Monolith** architecture. This ensures logical separation between the AI persona management, the market mechanism, and data ingestion, while deploying as a single artifact for ease of testing and prototype delivery.

### Core Modules (Package Structure)
The application is divided into specific feature modules. Communication between modules is handled strictly via public interfaces or Spring Events to ensure loose coupling.

```text
src/main/java/pl/msz/apc/
├── shared/                 # Shared Kernel (Value Objects, Base Events)
├── ingestion/              # Data parsing, Deep Research simulation, RAG
├── agents/                 # AI Persona definition, Prompt Engineering, LLM Client
├── market/                 # The core logic: Questions, Bets, Rounds, Consensus
└── reporting/              # Synthesis of results into MSZ-format scenarios
```

---

## 2. Tech Stack & Dependencies

*   **Language:** Java 21 (LTS).
*   **Framework:** Spring Boot 3.3.
*   **Build Tool:** Gradle (Kotlin DSL).
*   **LLM Integration:** **Spring AI** - Provides a portable abstraction layer. The system is designed to be vendor-agnostic, supporting providers like OpenAI, Azure OpenAI, Google Gemini, Ollama, etc., via configuration changes only.
*   **Database:** PostgreSQL 16+ with `pgvector` extension.
*   **ORM:** Spring Data JPA + Hibernate.
*   **Migration:** Liquibase.
*   **Boilerplate:** Project Lombok.
*   **Testing:** JUnit 5, Testcontainers (for Postgres).

---

## 3. Module Specifications

### 3.1. Module: `ingestion`
Responsible for loading the "world state" and context documents.

*   **Responsibilities:**
    *   Loading text files/PDFs.
    *   Chunking text for RAG.
    *   Generating embeddings using the configured **Embedding Model** (e.g., `text-embedding-3-small` or `embedding-001`).
    *   Storing vectors in PostgreSQL.
*   **Key Components:**
    *   `DocumentService`: Handles file upload and processing.
    *   `VectorStore`: Spring AI interface implementation using Postgres.
    *   `KnowledgeRetrievalService`: Public API to get relevant context for a specific query.

### 3.2. Module: `agents`
Manages the AI personas. This is the interface to the LLM Provider.

*   **Responsibilities:**
    *   Defining System Prompts (Personas: Economist, Skeptic, Strategist).
    *   Managing API Rate Limits.
    *   Injecting context (RAG) into prompts.
*   **Key Components:**
    *   `AgentFactory`: Creates instances of agents with specific `SystemPrompt` templates.
    *   `LlmClient`: Wrapper around Spring AI `ChatClient`.
    *   **Configuration:**
        *   **Fast Model:** Used for betting rounds (e.g., `gpt-4o-mini`, `gemini-1.5-flash`).
        *   **Reasoning Model:** Used for final synthesis (e.g., `gpt-4o`, `gemini-1.5-pro`).

### 3.3. Module: `market`
The core business logic. It does not know about "AI" directly; it asks the `agents` module for decisions.

*   **Domain Entities (JPA):**
    *   `PredictionMarket`: Represents one simulation run (e.g., "Atlantis 2025").
    *   `MarketQuestion`: A binary or probability question (e.g., "Will Oil < $30?").
    *   `AgentBet`: Stores the `agentId`, `confidence` (0.0-1.0), and `rationale` (Text).
*   **Logic:**
    *   **Round 1:** Broadcast questions to all agents. Collect bets.
    *   **Round 2 (Debate - Optional):** Broadcast the *divergence* (where agents disagreed most) and ask for updated bets.
    *   **Consensus Engine:** Calculates weighted averages based on Agent reliability (simple weights for prototype).

### 3.4. Module: `reporting`
Generates the final artifact required by MSZ.

*   **Responsibilities:**
    *   Aggregating `rationale` from `AgentBets`.
    *   Formatting the "Chain of Thought" text.
    *   Producing the JSON/Markdown output.

---

## 4. Database Schema (PostgreSQL)

```sql
-- Knowledge Base
CREATE TABLE documents (
    id UUID PRIMARY KEY,
    content TEXT,
    embedding VECTOR(1536) -- Dimension depends on the model (e.g., 1536 for OpenAI, 768 for Gemini)
);

-- Market Structure
CREATE TABLE simulation_runs (
    id UUID PRIMARY KEY,
    status VARCHAR(50),
    created_at TIMESTAMP
);

CREATE TABLE questions (
    id UUID PRIMARY KEY,
    simulation_id UUID REFERENCES simulation_runs(id),
    text VARCHAR(1000),
    category VARCHAR(50) -- ECONOMY, MILITARY, SOCIAL
);

CREATE TABLE bets (
    id UUID PRIMARY KEY,
    question_id UUID REFERENCES questions(id),
    agent_role VARCHAR(50), -- 'ECONOMIST', 'SKEPTIC'
    confidence DOUBLE PRECISION,
    rationale TEXT, -- CRITICAL: The "Chain of Thought"
    round_number INT
);
```

---

## 5. Deployment & Infrastructure

The application is containerized using Docker and orchestrated with Docker Compose.

### 5.1. Container Structure
*   **Application Container (`agentic-prediction-cloud`):**
    *   Base Image: `eclipse-temurin:21-jdk-alpine`
    *   Builds from the local Gradle project.
    *   Exposes port 8080 (mapped to host 8080).
    *   Depends on the `data` network.
    *   **Environment Variables:**
        *   `SPRING_PROFILES_ACTIVE`: `prod` or `dev`.
        *   `SPRING_AI_OPENAI_API_KEY` / `SPRING_AI_GEMINI_API_KEY`: Injected at runtime.
*   **Database Container (`data-postgres`):**
    *   Image: `pgvector/pgvector:pg16` (PostgreSQL 16 with vector extension).
    *   Exposes port 5432.
    *   Persists data to a named volume `pgdata`.
    *   Initializes schemas (`app`, `app_dev`) via `init.sql`.

### 5.2. Deployment Scripts
*   `deploy.ps1`: PowerShell script to build the JAR and restart containers.
*   `docker/compose-data.yml`: Defines the persistent database service.
*   `docker/compose-prod.yml`: Defines the application service for production profile.

### 5.3. Profiles
*   **prod:** Uses the `app` schema.
*   **dev:** Uses the `app_dev` schema (isolated environment).

---

## 6. API Design (REST)

Since there is no UI yet, the API drives the process.

**POST** `/api/v1/simulation/start`
*   Input: `{"scenario_constraints": [...], "year_horizon": 2026}`
*   Output: `simulationId`

**POST** `/api/v1/simulation/{id}/ingest`
*   Body: Multipart File (PDF/TXT)
*   Action: Triggers RAG processing.

**POST** `/api/v1/simulation/{id}/run-market`
*   Action:
    1.  Generates Questions (via Orchestrator Agent).
    2.  Spawns Agents.
    3.  Collects Bets.
*   Output: `status: "PROCESSING"` (Async)

**GET** `/api/v1/simulation/{id}/report`
*   Output: JSON containing the 4 Scenarios, Recommendations, and the full Chain of Thought (compiled from Bet Rationales).

---

## 7. Implementation Strategy (Hackathon Mode)

1.  **Skeleton:** Initialize Spring Boot with `spring-boot-starter-data-jpa`, `spring-boot-starter-web`, `spring-ai-starter` (generic), `lombok`.
2.  **Database:** Run Postgres locally via Docker Compose.
3.  **Spring AI Setup:** Configure `application.yml` with generic properties, allowing injection of specific vendor keys.
4.  **Mocking (Speed):**
    *   Create a `MockIngestionService` first that uses hardcoded text strings about Atlantis to test the Market Logic without waiting for file parsers.
5.  **Agent Loop:**
    *   Implement the `AgentService` to take a `Question`, fetch `Context`, and return a `Bet` object using the Chat Client's structured output capabilities.

## 8. Scalability & Security Notes

*   **Scalability:** The stateless nature of the `agents` module means we can horizontally scale the application. The bottleneck is the Database (solved by Postgres robustness) and API Rate Limits (solved by implementing a Rate Limiter in the `LlmClient`).
*   **Security:**
    *   API Keys are injected via Environment Variables.
    *   Prompts are sanitized.
    *   In the future, the `LlmClient` interface can be swapped for an `OllamaClient` implementation to run local models (Llama 3) for air-gapped security without changing business logic.

### 7.2. Deployment Scripts
*   `deploy.ps1`: PowerShell script to build the JAR and restart containers.
*   `docker/compose-data.yml`: Defines the persistent database service.
*   `docker/compose-prod.yml`: Defines the application service for production profile.

### 7.3. Profiles
*   **prod:** Uses the `app` schema.
*   **dev:** Uses the `app_dev` schema (isolated environment).

## 8. Scalability & Security Notes

*   **Scalability:** The stateless nature of the `agents` module means we can horizontally scale the application. The bottleneck is the Database (solved by Postgres robustness) and API Rate Limits (solved by implementing a Rate Limiter in the `GeminiClient`).
*   **Security:**
    *   API Keys are injected via Environment Variables.
    *   Prompts are sanitized.
    *   In the future, the `GeminiClient` interface can be swapped for an `OllamaClient` implementation to run local models (Llama 3) for air-gapped security without changing business logic.

