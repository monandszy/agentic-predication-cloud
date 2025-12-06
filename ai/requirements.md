# Requirements Document

## Introduction
Agent Prediction Cloud (APC) is a Multi-Agent System (MAS) that simulates a prediction market. It uses a group of specialized AI Agents (Personas) to "bet" on the probability of future events based on input data (documents, news). The system aggregates these bets and rationales to produce high-quality, explainable scenarios for the Ministry of Foreign Affairs (MSZ), mitigating the risk of single-model hallucinations.

## Requirements

### 1. Data Ingestion & Knowledge Base
1.  **Document Loading**
    *   **User Story:** As an administrator, I want to load text documents (PDF, TXT) into the system so that agents have a knowledge base to analyze.
    *   **Acceptance Criteria:**
        *   WHEN a file is placed in the ingestion folder or uploaded via API, THEN the system SHALL parse the text content.
        *   WHEN the text is parsed, THEN it SHALL be chunked into appropriate sizes for embedding.

2.  **Vector Storage**
    *   **User Story:** As a system, I want to store document chunks as vectors so that relevant context can be retrieved for agents (RAG).
    *   **Acceptance Criteria:**
        *   WHEN a document chunk is processed, THEN the system SHALL generate an embedding using the configured AI model.
        *   WHEN an embedding is generated, THEN it SHALL be stored in the PostgreSQL database with `pgvector`.

### 2. Agent Management
3.  **Persona Definition**
    *   **User Story:** As a developer, I want to define different agent personas (e.g., Economist, Skeptic) so that the analysis covers multiple perspectives.
    *   **Acceptance Criteria:**
        *   WHEN an agent is instantiated, THEN it SHALL be initialized with a specific System Prompt defining its role and bias.
        *   The system SHALL support at least 5 distinct personas: Skeptic, Economist, Strategist, Social Observer, Patriot.

4.  **LLM Integration**
    *   **User Story:** As a system, I want to communicate with an LLM (Google Gemini) so that agents can generate responses.
    *   **Acceptance Criteria:**
        *   WHEN an agent needs to make a decision, THEN the system SHALL send a prompt to the LLM API.
        *   The system SHALL handle API rate limits and errors gracefully.
        *   The system SHALL cache LLM responses to avoid redundant API calls and reduce latency.

### 3. Market Mechanism
5.  **Scenario/Question Generation (Market Maker)**
    *   **User Story:** As a user, I want the system to generate binary or conditional questions based on the input data so that agents have specific events to predict.
    *   **Acceptance Criteria:**
        *   WHEN the "Market Maker" phase runs, THEN the system SHALL analyze the knowledge base and produce a list of prediction questions.

6.  **Betting (The Betting Floor)**
    *   **User Story:** As a user, I want agents to place bets on questions with a confidence score and rationale so that I understand their reasoning.
    *   **Acceptance Criteria:**
        *   WHEN a question is presented to an agent, THEN the agent SHALL return a Confidence Score (0-100%) and a text Rationale.
        *   The Rationale SHALL reference specific information from the knowledge base (if applicable).

7.  **Debate Rounds**
    *   **User Story:** As a user, I want agents to see other agents' bets and update their own so that a consensus (or clear divergence) can be reached.
    *   **Acceptance Criteria:**
        *   WHEN the first round is complete, THEN the system SHALL expose the bets and rationales to all agents.
        *   WHEN the second round runs, THEN agents SHALL provide updated scores and rationales based on the new information.

### 4. Reporting & Synthesis
8.  **Result Aggregation**
    *   **User Story:** As a user, I want to see a weighted average of the predictions so that I have a single probability metric.
    *   **Acceptance Criteria:**
        *   WHEN the betting rounds are finished, THEN the system SHALL calculate the final probability for each scenario.

9.  **Chain of Thought Report**
    *   **User Story:** As a user, I want a narrative report explaining the consensus and the reasoning behind it so that the prediction is explainable.
    *   **Acceptance Criteria:**
        *   WHEN the process is complete, THEN the system SHALL generate a report synthesizing the "Betting Rationales" into a coherent narrative.
        *   The report SHALL highlight key disagreements (if any).

### 5. Infrastructure & Non-Functional
10. **Docker Deployment**
    *   **User Story:** As a DevOps engineer, I want to deploy the system using Docker Compose so that it is easy to run in different environments.
    *   **Acceptance Criteria:**
        *   WHEN `docker-compose up` is run, THEN the application and database SHALL start and communicate correctly.
        *   The database SHALL persist data across restarts.

11. **Modular Architecture**
    *   **User Story:** As a developer, I want the code to be organized in modules (ingestion, agents, market, reporting) so that the codebase is maintainable.
    *   **Acceptance Criteria:**
        *   The project structure SHALL follow the defined package structure.
        *   Dependencies between modules SHALL be minimized and explicit.
