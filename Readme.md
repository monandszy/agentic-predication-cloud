# Projekt dla Ministerstwa Spraw Zagranicznych - “Scenariusze jutra”. Hackaton prototype

# Specyfikacja Techniczna: Agent Prediction Cloud (APC)
**Moduł Analityczny dla projektu "Scenariusze Jutra"**

### 1. Koncepcja Architektoniczna: Syntetyczny Rynek Predykcyjny
APC to system **Multi-Agent Systems (MAS)**, który symuluje działanie rynku predykcyjnego (prediction market). Zamiast polegać na jednej "halucynacji" modelu LLM, system powołuje do życia grupę wyspecjalizowanych Agnetów-Person, którzy "zakładają się" o prawdopodobieństwo wystąpienia przyszłych zdarzeń.

#### Faza 1: The Market Maker (Orchestrator)
*   **Rola:** Analizuje dane wejściowe (zbiór dokumentów, newsy, założenia "sztywne" dla Atlantis), mające znaczenie dla domeny urzytkownika.
*   **Zadanie:** Generuje listę Scenariuszy w formie **Pytan Binarnych** lub **Pytan Warunkowych**.
*   **Przykład:** *"Czy w oparciu o spadek PKB strefy Euro, bezrobocie w Atlantis przekroczy 12% w ciągu 12 miesięcy?"*

#### Faza 2: The Traders (Agent Swarm)
Zbiór niezależnych instancji LLM (w prototypie: wywołania API z różnymi System Prompts). Każdy agent analizuje pytania przez pryzmat swojej specjalizacji. Future feature: Agent Persona manager & generator.
*   **Agent A (The Skeptic/Counter-Intel):** Szuka dezinformacji, data poisoning i sprzeczności. Działa jako filtr bezpieczeństwa.
*   **Agent B (The Economist):** Priorytetyzuje PKB, handel i surowce (zorientowany na zysk).
*   **Agent C (The Strategist/Hawk):** Patrzy przez pryzmat bezpieczeństwa militarnego i sojuszy (NATO).
*   **Agent D (The Social Observer):** Analizuje nastroje społeczne, ryzyko strajków i stabilność rządu.
*   **Agent E (Atlantis Patriot):** Priorytetem jest wyłącznie interes narodowy (zgodnie z wytycznymi MSZ).

#### Faza 3: The Betting Floor (Interakcja)
*   Każdy agent składa "zakład" (Confidence Score 0-100%) na każde pytanie.
*   **CRITICAL FEATURE:** Każdy zakład musi zawierać uzasadnienie tekstowe (**Betting Rationale**).
*   Agenci widzą nawzajem swoje zakłady i mogą dokonać jednej lub więcej rund "korekty" (debaty), jeśli rozbieżności są skrajne (>40%).

#### Faza 4: The Synthesizer (Raportowanie)
*   Agreguje wyniki (średnia ważona zakładów).
*   Generuje **Chain of Thought** poprzez syntezę "Betting Rationales" od poszczególnych agentów.
*   Buduje ostateczny tekst scenariuszy (Pozytywny/Negatywny).

### 3. Realizacja Wymagań MSZ przez APC

| Wymóg MSZ | Realizacja w Agent Prediction Cloud |
| :--- | :--- |
| **Wyjaśnialność (Chain of Thought)** | "Betting Logs" – system pokazuje: *"Agent Ekonomista dał 80% szans na kryzys, bo znalazł korelację między ceną ropy a inflacją w Atlantis."* |
| **Data Poisoning (Bonus)** | Agent "The Skeptic" ma za zadanie obniżać wagi informacji (Low Confidence Bet) pochodzących z podejrzanych źródeł lub niespójnych logicznie. |
| **Wieloczynnikowość** | Konflikt interesów między Agentem Ekonomicznym a Militarnym naturalnie symuluje złożoność geopolityki (dylemat "masło czy armaty"). |
| **Skalowalność** | Dodanie nowego kraju do analizy to po prostu dodanie nowego Agenta (np. "German Policy Agent") do puli traderów. |

### 4. Ryzyka i Mitygacja (Dla Prototypu)

*   **Ryzyko:** Agenci będą "halucynować" te same błędy (Groupthink).
*   **Mitygacja:** Wymuszenie w promptach "Adwersarza" (jeden agent musi zawsze szukać kontrargumentów).
*   **Ryzyko:** Zbyt długi czas przetwarzania przy wielu zapytaniach API.
*   **Mitygacja:** Ograniczenie liczby pytań w "Rynku" do 5-10 kluczowych dla demo.