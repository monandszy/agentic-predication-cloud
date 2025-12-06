# Projekt dla Ministerstwa Spraw Zagranicznych - “Scenariusze jutra”. Hackaton prototype

### 1. Klient i Użytkownik Końcowy
*   **Instytucja:** Ministerstwo Spraw Zagranicznych (MSZ).
*   **Przyszli Użytkownicy:** Setki pracowników MSZ (zarówno w centrali w kraju, jak i na placówkach zagranicznych).
*   **Obecny model pracy:** Praca manualna polegająca na wyszukiwaniu i przetwarzaniu informacji, zajmująca dziesiątki tysięcy godzin rocznie.

### 2. Zdefiniowany Problem (Stan Obecny)
*   **Ograniczenia ludzkie:** Podstawowym „narzędziem” jest obecnie ludzki umysł, co uniemożliwia przetworzenie tysięcy stron danych i uwzględnienie wszystkich zmiennych.
*   **Niedoskonałość narzędzi:** Obecne systemy są mało wyrafinowane, opierają się na prostych założeniach i analizują ograniczoną liczbę parametrów.
*   **Ryzyko:** Możliwość pominięcia kluczowych informacji, co prowadzi do błędnych wniosków i rekomendacji.
*   **Jakość prognoz:** Obecne prognozy są często zbyt uproszczone, nie wyjaśniają przyczyn trendów i nie uwzględniają interakcji wieloczynnikowych (zmiennych społecznych, wielostronnych).
*   **Brak transparentności:** Dostępne narzędzia nie tłumaczą powiązań między danymi wejściowymi, co obniża wiarygodność analiz dla decydentów.

### 3. Oczekiwany rezultat
*   **Rozwiązanie:** Stworzenie narzędzia do analizy danych tekstowych i liczbowych, oferującego wieloaspektowe scenariusze zdarzeń przyszłych o realistycznym poziomie probabilizmu.

*   **Forma narzędzia:** Aplikacja / agent, który analizuje dane wejściowe (opisane w `Atlantis_Scenarios.md`) i wyciąga wnioski wg kryteriów ("interes państwa Atlantis") i wag.

*   **Produkt końcowy:**
Scenariusze (predykcje) i rekomendacje dla rządu Atlantis w dwóch perspektywach (12 i 36 miesięcy) i dwóch wariantach (negatywny/pozytywny). Łącznie 4 scenariusze.

### 4. Cel Wyzwania
*   **Produkt:** Stworzenie narzędzia do **analizy foresightowej** (prognozowania).
*   **Główne zadanie narzędzia:** Automatyczna integracja dużej liczby parametrów i zależności w celu generowania scenariuszy sytuacji międzynarodowej.
*   **Kontekst konkurencyjny:** Dyplomacje innych krajów wdrażają już podobne rozwiązania; brak takiego narzędzia w MSZ grozi utratą pozycji i „zostaniem z tyłu”.

### 5. Wymagania Funkcjonalne i Techniczne
*   **Horyzont czasowy prognoz:** Narzędzie musi generować scenariusze na **12 lub 36 miesięcy**.
*   **Obszary analizy:** Musi uwzględniać czynniki polityczne, gospodarcze, społeczne i obronne.
*   **Skalowalność:** Narzędzie musi być zaprojektowane tak, aby w przyszłości analizować **50- lub 100-krotnie większą liczbę parametrów** w porównaniu do prototypu z hackathonu.
*   **Wyjaśnialność (Explainability):** System musi ukazywać powiązania między danymi wejściowymi a wynikiem (nie może być tzw. „czarną skrzynką”), aby budować wiarygodność decyzji strategicznych.
*   **Złożoność:** Narzędzie musi obsługiwać interakcje wieloczynnikowe (zamiast prostych korelacji).
* **Wymagania dla modelu:**
  Ustawienie „temperatury” na realizm (z opcją jednego scenariusza "kreatywnego") ,Zachowanie zasady **Chain of Thought**.

### 6. Oczekiwane Korzyści Biznesowe
*   Działanie wyprzedzające (proaktywne) zamiast reaktywnego.
*   Szybsze odkodowywanie symptomów przyszłych wydarzeń.
*   Zwiększenie trafności rekomendacji strategicznych dla polityki zagranicznej.

### 7. Wymagane Technologie i Metodyka
*   **Kluczowe technologie:** Narzędzie musi wykorzystywać **NLP** (przetwarzanie języka naturalnego), zaawansowaną analizę danych typu **Deep Research** oraz **modelowanie scenariuszy**.
*   **Cel technologiczny:** Wykrywanie nieoczywistych czynników zmian w dużych wolumenach tekstu.
*   **Zadanie:** Typowanie prawdopodobnych wydarzeń i trendów w polityce międzynarodowej.

### 8. Cecha Krytyczna: Wyjaśnialność (Chain of Thought)
*   **Priorytet:** Jest to najważniejsza cecha z punktu widzenia użytkownika końcowego.
*   **Wymóg:** System nie może być „czarną skrzynką”. Musi prezentować pełny łańcuch myślowy (**Chain of Thought**).
*   **Elementy wyjaśnialności:**
 *   Prezentacja ścieżki od faktów historycznych do faktów spodziewanych.
 *   Opis zidentyfikowanych korelacji między przeszłością a przyszłością.
 *   Logiczne uzasadnienie generowanych wniosków.

### 9. Bezpieczeństwo, Suwerenność Danych i Odporność
*   **Priorytet absolutny:** Gwarancja poufności zapytań (promptów) oraz generowanych analiz. Żaden podmiot zewnętrzny (w tym dostawca chmury/modelu) nie może mieć wglądu w proces wnioskowania.
*   **Suwerenność danych:** Wyłączna własność MSZ nad historią zapytań; brak możliwości wykorzystania danych MSZ do trenowania publicznych modeli AI.
*   **Architektura:** Gotowość rozwiązania do pracy w środowisku izolowanym (konteneryzacja) oraz na danych zamkniętych (opcja *air-gapped* - odłączenie od sieci publicznej).
*   **Ochrona przed dezinformacją (Data Poisoning):** Zaimplementowanie mechanizmów weryfikacji źródeł, mających na celu uodpornienie systemu na celowe "zatruwanie" danych przez wrogich aktorów państwowych (np. wykrywanie sprzeczności i anomalii w źródłach open-source).