# ShowCaseSpringAngularApp
Just a simple showcase fullstack app built in SpringBoot and Angular.
# TODO
The app is all good to go but need to conifgure the docker enviroment so it can be run from one place with one command. 
And fix the repository. For now its better to download separetly fron branch backend and branch frontend.
# The rest of description is in Polish
# ------------------------------------
# Backend Showcasow'ej aplikacji wyszukiwarki Ofert Pracy w Spring'u

Backendowa część aplikacji typu "Job Board" służącej do wyszukiwania i filtrowania ofert pracy w branży IT. Aplikacja została zbudowana w oparciu o Spring Boot 3 i architekturę REST API.

🚀 Technologie

Java 21+

Spring Boot 3.x (Web, Data JPA, Security, Validation)

Baza Danych: H2 (In-Memory)

ℹ️ Elastyczność SQL: Aplikacja jest w pełni zgodna ze standardem SQL. Dzięki warstwie abstrakcji Spring Data JPA (Hibernate), możliwa jest bezproblemowa migracja z H2 na produkcyjne bazy danych takie jak PostgreSQL, Microsoft SQL Server czy MySQL. Wymaga to jedynie zmiany sterownika i ustawień w application.properties.

Bezpieczeństwo: JWT (JSON Web Token) + Spring Security

Generowanie Danych: Java Faker (Data Seeder)

Narzędzia: Lombok, Maven

⚙️ Konfiguracja i Uruchomienie

1. Plik application.properties

Projekt korzysta z pliku konfiguracyjnego, który nie jest dołączony do repozytorium ze względów bezpieczeństwa.
Aby uruchomić aplikację, utwórz plik src/main/resources/application.properties na podstawie szablonu i uzupełnij go:

spring.application.name=WyszukiwarkaPrac

# --- Baza Danych (Domyślnie H2, łatwa zamiana na Postgres/MSSQL) ---
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa   <-- Twoj login do bazy
spring.datasource.password=user <-- Twoje hasło do bazy

# --- Konsola H2 ---
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# --- JPA ---
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# --- JWT Security ---
# Wygeneruj silny klucz (min. 32 znaki) dla algorytmu HS256
jwt.secret-key= (klucz do podpisu tokenow)


2. Data Seeder (Dane Startowe)

Aplikacja posiada wbudowany Data Seeder, który przy każdym uruchomieniu:

Wypełnia bazę (H2) losowymi ofertami pracy, firmami i technologiami.

Tworzy domyślnego użytkownika administracyjnego.

Domyślne dane logowania (stworzone przez Seeder):

Email: admin@admin.pl

Hasło: admin123

🛡️ Bezpieczeństwo i Architektura

Aplikacja wykorzystuje hybrydowy model bezpieczeństwa:

Dla API (/api/**): Model bezstanowy (Stateless) oparty o tokeny JWT. Każde żądanie do chronionego zasobu musi zawierać nagłówek Authorization: Bearer <token>.

Dla H2 Console (/h2-console): Model stanowy (Stateful) oparty o sesję i formularz logowania (formLogin), co umożliwia wygodny dostęp do bazy przez przeglądarkę.

🔌 Endpointy API

Adres bazowy: http://localhost:8080

1. Autentykacja (Publiczne)

Metoda

Endpoint

Opis

Przykładowe Body

POST

/api/auth/rejestracja

Rejestracja nowego użytkownika

{"email": "user@test.pl", "password": "123"}

POST

/api/auth/login

Logowanie (zwraca JWT)

{"email": "admin@admin.pl", "password": "admin123"}

Odpowiedź logowania:

{
"token": "eyJhbGciOiJIUzI1NiJ9..."
}


2. Oferty Pracy (Publiczne)

Endpoint obsługuje paginację i dynamiczne filtrowanie. Wszystkie parametry są opcjonalne.

GET /api/oferty

Parametry URL (Query Params):

page: Numer strony (od 0, domyślnie 0)

size: Rozmiar strony (domyślnie 20)

kodWoj: Kod województwa (np. MZ)

nazwaMiasta: Nazwa miasta (np. Warszawa)

nazwaFirmy: Nazwa firmy (np. Google)

minWidelki: Minimalne wynagrodzenie

maxWidelki: Maksymalne wynagrodzenie

Przykłady:

Wszystkie oferty: GET /api/oferty

Oferty z Warszawy: GET /api/oferty?nazwaMiasta=Warszawa

Filtrowanie zaawansowane: GET /api/oferty?kodWoj=MZ&minWidelki=10000&page=1

3. Ulubione Oferty (Chronione 🔒)

Wymagają nagłówka: Authorization: Bearer <twoj_token_jwt>

Metoda

Endpoint

Opis

GET

/api/ulubione

Pobiera listę ofert polubionych przez zalogowanego użytkownika (paginowana).

POST

/api/ulubione/{id}

Dodaje ofertę o podanym ID do ulubionych.

DELETE

/api/ulubione/{id}

Usuwa ofertę z ulubionych.

🛢️ Dostęp do Bazy Danych (H2 Console)

Wejdź na http://localhost:8080/h2-console.

Zaloguj się formularzem Spring Security (dane admina z Seedera: admin@admin.pl / admin123).

W drugim oknie logowania (H2) wpisz dane z application.properties:

JDBC URL: jdbc:h2:mem:testdb

User: user (lub Twój z configu)

Password: twoje_haslo (lub Twoje z configu)

# ----------------
# branch frontend

💻 Frontend Wyszukiwarki Ofert Pracy IT

Warstwa kliencka aplikacji typu "Job Board", zbudowana w oparciu o Angular 18+ i Material Design. Aplikacja komunikuje się z backendem Spring Boot poprzez REST API zabezpieczone tokenami JWT.

🚀 Technologie i Biblioteki

Framework: Angular 18+ (Standalone Components)

Stylizacja: Angular Material 3 (SASS/SCSS)

Komunikacja HTTP: HttpClient, Interceptory (JWT)

Formularze: Reactive Forms

Zarządzanie Stanem: RxJS (BehaviorSubject) + Serwisy (State Caching)

Ikony: Material Icons

✨ Główne Funkcjonalności

1. Wyszukiwarka i Filtrowanie (Homepage)

Dynamiczna tabela ofert oparta o MatTable.

Server-side Pagination: Obsługa dużych zbiorów danych (ładowanie stron "na żądanie" z backendu).

Zaawansowane filtry: Miasto, Firma, Województwo, Widełki płacowe (Suwak dwuzakresowy).

State Caching: Aplikacja zapamiętuje ustawione filtry i numer strony po przejściu do szczegółów oferty i powrocie.

2. Autentykacja i Bezpieczeństwo

Logowanie i Rejestracja: Formularze z pełną walidacją (Reactive Forms).

JWT Handling: Automatyczne dołączanie tokena do zapytań (AuthInterceptor).

Strażnik widoku: Ukrywanie/pokazywanie elementów interfejsu (np. przycisk "Ulubione") w zależności od stanu zalogowania.

3. System Ulubionych

Toggle Like: Możliwość dodawania/usuwania ofert z ulubionych jednym kliknięciem serduszka.

Widok Ulubionych: Dedykowany widok filtrujący tylko polubione oferty (wykorzystuje ten sam komponent Homepage w trybie favorites).

4. Szczegóły Oferty

Lazy Loading: Pełne dane oferty (opis, technologie) są pobierane z API dopiero w momencie kliknięcia przycisku "Szczegóły".

Prezentacja w estetycznym oknie dialogowym (MatDialog).

⚙️ Instalacja i Uruchomienie

Wymagania wstępne

Node.js (v18 lub nowszy)

Angular CLI (npm install -g @angular/cli)

Działający backend Spring Boot na porcie 8080

Krok po kroku

Instalacja zależności:

npm install


Uruchomienie serwera deweloperskiego:

ng serve


Aplikacja będzie dostępna pod adresem: http://localhost:4200/

🔧 Konfiguracja Środowiska

Adres API backendu jest skonfigurowany w pliku src/environments/environment.ts.

export const environment = {
production: false,
apiUrl: 'http://localhost:8080'
};


📂 Struktura Projektu

Projekt wykorzystuje architekturę Standalone Components (brak AppModule).

src/app/core/ - Globalne serwisy (Auth), interceptory, guardy.

src/app/modules/auth/ - Komponenty logowania i rejestracji.

src/app/modules/homepage/ - Główny widok, tabela, filtry, serwisy ofert.

src/app/modules/core/components/ - Komponenty współdzielone (Header, Popup).
