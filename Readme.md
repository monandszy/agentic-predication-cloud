# Agentic Prediction Cloud (APC) - "Scenariusze Jutra"
**Prototyp na Hackathon dla Ministerstwa Spraw Zagranicznych**

APC to zaawansowany system **Multi-Agent Systems (MAS)**, który symuluje działanie rynku predykcyjnego (prediction market) oraz generuje strategiczne raporty foresightowe. System wykorzystuje grupę wyspecjalizowanych Agentów-Person (LLM), którzy analizują dane, debatują i "zakładają się" o prawdopodobieństwo wystąpienia przyszłych zdarzeń, eliminując ryzyko halucynacji pojedynczego modelu.

---

## 1. Koncepcja Architektoniczna: Syntetyczny Rynek Predykcyjny

System działa w czterech fazach, symulując proces analityczny w MSZ:

### Faza 1: The Market Maker (Orchestrator)
*   **Rola:** Analiza danych wejściowych (dokumenty, newsy, założenia strategiczne).
*   **Zadanie:** Ekstrakcja faktów i generowanie scenariuszy w formie pytań binarnych lub warunkowych.
*   **Przykład:** *"Czy w oparciu o spadek PKB strefy Euro, bezrobocie w Atlantis przekroczy 12% w ciągu 12 miesięcy?"*

### Faza 2: The Traders (Agent Swarm)
Zbiór niezależnych instancji LLM z unikalnymi osobowościami (Personas):
*   **The Skeptic (Counter-Intel):** Wykrywa dezinformację, data poisoning i sprzeczności.
*   **The Economist:** Analizuje PKB, handel i surowce (zysk/strata).
*   **The Strategist (Hawk):** Skupia się na bezpieczeństwie militarnym i sojuszach (NATO).
*   **The Social Observer:** Analizuje nastroje społeczne i stabilność rządu.
*   **The Patriot:** Priorytetyzuje wyłączny interes narodowy Atlantis.

### Faza 3: The Betting Floor (Interakcja)
*   Agenci składają "zakłady" (Confidence Score 0-100%) wraz z uzasadnieniem (**Betting Rationale**).
*   System umożliwia rundy debaty, gdzie agenci widzą swoje argumenty i mogą korygować stanowiska.

### Faza 4: The Synthesizer (Raportowanie)
*   Agregacja wyników (średnia ważona, konsensus).
*   Generowanie **Chain of Thought** poprzez syntezę uzasadnień agentów.
*   Tworzenie końcowego raportu (Scenariusze Pozytywne/Negatywne).

---

## 2. Realizacja Wymagań MSZ

| Wymóg MSZ | Realizacja w Agent Prediction Cloud |
| :--- | :--- |
| **Wyjaśnialność (Chain of Thought)** | System prezentuje logikę każdego "zakładu" i syntezę argumentów w raporcie końcowym. |
| **Data Poisoning (Bonus)** | Agent "The Skeptic" aktywnie obniża wagi informacji z podejrzanych źródeł. |
| **Wieloczynnikowość** | Konflikt interesów między wyspecjalizowanymi agentami symuluje złożoność geopolityki. |
| **Skalowalność** | Łatwe dodawanie nowych agentów (np. "German Policy Agent") do puli. |

---

## 3. Skalowalność i Rozwój (Scalability)

Architektura APC została zaprojektowana z myślą o łatwym skalowaniu horyzontalnym i wertykalnym, aby sprostać rosnącym wymaganiom MSZ:

*   **Modularność Agentów:** Dodanie nowej perspektywy (np. "Ekspert ds. Chin" lub "Analityk Klimatyczny") wymaga jedynie zdefiniowania nowego `Persona` w kodzie. System automatycznie włączy nowego agenta do procesu debaty i głosowania.
*   **Przetwarzanie Równoległe:** Każdy agent działa niezależnie. W środowisku produkcyjnym (np. Kubernetes), każdy agent może być osobnym mikroserwisem lub wątkiem, co pozwala na równoległą analizę tysięcy dokumentów.
*   **Obsługa Dużych Danych (Big Data):** Moduł `FactProcessor` może zostać łatwo zintegrowany z wektorowymi bazami danych (np. Pinecone, Milvus) w celu obsługi RAG (Retrieval-Augmented Generation) na zbiorach milionów dokumentów, zamiast prostego przetwarzania tekstu wejściowego.
*   **Konteneryzacja:** Aplikacja jest gotowa do wdrożenia w kontenerach Docker, co ułatwia zarządzanie zasobami i izolację środowisk (wymóg bezpieczeństwa).

---

## 4. Możliwości API i Użycie (API Capabilities)

System udostępnia interfejs REST API udokumentowany w Swagger UI (dostępny pod adresem `http://localhost:8080/swagger-ui.html` po uruchomieniu lokalnym).

### Generowanie Raportu Strategicznego (Nowy Scenariusz)
Generuje kompleksowy raport strategiczny na podstawie surowego tekstu wejściowego (faktów), wykorzystując system wieloagentowy do syntezy scenariuszy.

*   **Endpoint:** `POST /api/v1/scenarios/strategic`
*   **Opis:** Wyodrębnia fakty z dostarczonego tekstu, generuje perspektywy agentów (Ekonomista, Sceptyk, Strateg, Futurysta) i syntezuje końcowy raport z wariantami Pozytywnym/Negatywnym na 12 i 36 miesięcy.
*   **Parametry:**
    *   `focus` (query, opcjonalny): Strategiczny punkt ciężkości raportu (domyślnie: "interes państwa Atlantis").
*   **Body:** Surowa treść tekstowa (np. opis scenariusza Atlantis).

### Symulacja Rynku (Stary Scenariusz)
Uruchamia symulację rynku predykcyjnego, w której agenci obstawiają binarne pytania wygenerowane na podstawie tematu lub kontekstu.

*   **Endpoint:** `POST /api/v1/markets/simulate`
    *   **Opis:** Tworzy rynek dla danego tematu, automatycznie pobiera kontekst (lub korzysta z wiedzy ogólnej) i uruchamia symulację.
    *   **Parametry:** `topic` (query).

*   **Endpoint:** `POST /api/v1/markets/simulate/context`
    *   **Opis:** Uruchamia symulację rynku, wykorzystując jawnie dostarczony tekst jako kontekst.
    *   **Body:** Surowy tekst kontekstu.
    *   **Parametry:** `topic` (query).

### Podsumowanie Przepływu Pracy (Workflow)
1.  **Ekstrakcja Faktów:** System analizuje surowy tekst, aby wyodrębnić kluczowe fakty.
2.  **Obrady Agentów:** Wyspecjalizowani agenci (Persony) analizują fakty/pytania.
3.  **Synteza/Konsensus:**
    *   **Raporty Strategiczne:** LLM syntezuje wyniki pracy agentów w ustrukturyzowany raport Markdown.
    *   **Rynki:** Algorytm konsensusu oblicza prawdopodobieństwa na podstawie zakładów agentów.

---

## 5. Jak Uruchomić (How to Run)

### Wymagania Wstępne
*   Java 17+
*   Docker (do bazy danych, opcjonalnie jeśli używasz H2)
*   Klucz API OpenAI / Gemini (skonfigurowany w `application.yml`)

### Budowanie i Uruchamianie
```bash
```bash
# Zbuduj projekt
./gradlew build

# Uruchom aplikację
./gradlew bootRun
```

### Testowanie
```bash
# Uruchom testy
./gradlew test
```
```

### Testowanie
```bash
# Uruchom testy
./gradlew test
```