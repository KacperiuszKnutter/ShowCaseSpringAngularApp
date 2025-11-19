package com.politechnika_warszawska.wyszukiwarkaprac.config;

import com.github.javafaker.Faker;
import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.*;
import com.politechnika_warszawska.wyszukiwarkaprac.repositories.*;
import com.politechnika_warszawska.wyszukiwarkaprac.services.UzytkownikService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// klasa na potrzeby korzystania z h2 w ram, tworzylem ja tylko po to zeby testowac sobie dodawanie
// sztucznych rekordow, normalnie mozna smialo MS SQL Server lub Postgres
@Component
public class DataSeeder implements CommandLineRunner {

    private final WojewodztwoDBRepository wojewodztwoRepository;
    private final MiastoDBRepository miastoRepository;
    private final FirmaDBRepository firmaRepository;
    private final TechnologiaRepository technologiaRepository;
    private final OfertyPracyRepository ofertaPracyRepository;
    private final UzytkownikDBRepository uzytkownikRepository;
    private final UzytkownikService uzytkownikService;

    // Wstrzykujemy wszystkie potrzebne repozytoria
    public DataSeeder(WojewodztwoDBRepository wojewodztwoRepository,
                      MiastoDBRepository miastoRepository,
                      FirmaDBRepository firmaRepository,
                      TechnologiaRepository technologiaRepository,
                      OfertyPracyRepository ofertaPracyRepository,
                      UzytkownikDBRepository ur,
                      UzytkownikService uzytkownikService) {
        this.wojewodztwoRepository = wojewodztwoRepository;
        this.miastoRepository = miastoRepository;
        this.firmaRepository = firmaRepository;
        this.technologiaRepository = technologiaRepository;
        this.ofertaPracyRepository = ofertaPracyRepository;
        this.uzytkownikService = uzytkownikService;
        this.uzytkownikRepository = ur;
    }

    @Override
    public void run(String... args) throws Exception {
        // Używamy count() aby uruchomić seeder tylko raz, gdy baza jest pusta
        if (wojewodztwoRepository.count() == 0) {
            ladujDane();
        }
    }

    private void ladujDane() {
        Faker faker = new Faker(new Locale("pl-PL"));

        // 1. WOJEWÓDZTWA
        List<WojewodztwoDB> wojewodztwa = ladujWojewodztwa();

        // 2. MIASTA
        List<MiastoDB> miasta = ladujMiasta(wojewodztwa);

        // 3. FIRMY
        List<FirmaDB> firmy = ladujFirmy(miasta, faker);

        // 4. TECHNOLOGIE
        List<TechnologiaDB> technologie = ladujTechnologie();

        // 5 & 6. OFERTY PRACY + LISTY TECHNOLOGII
        ladujOfertyPracy(firmy, technologie, faker);

        System.out.println(" Baza danych została pomyślnie wypełniona danymi!");

        if (uzytkownikRepository.count() == 0) {
            System.out.println(">>> Tworzenie domyślnego użytkownika admin@admin.pl...");
            uzytkownikService.registerUser("admin@admin.pl", "admin123");
        }

    }

    // 1. WOJEWÓDZTWA
    private List<WojewodztwoDB> ladujWojewodztwa() {
        Map<String, String> woj = Map.ofEntries(
                Map.entry("DS", "Dolnośląskie"), Map.entry("KP", "Kujawsko-pomorskie"),
                Map.entry("LU", "Lubelskie"), Map.entry("LB", "Lubuskie"),
                Map.entry("LD", "Łódzkie"), Map.entry("MA", "Małopolskie"),
                Map.entry("MZ", "Mazowieckie"), Map.entry("OP", "Opolskie"),
                Map.entry("PK", "Podkarpackie"), Map.entry("PL", "Podlaskie"),
                Map.entry("PM", "Pomorskie"), Map.entry("SL", "Śląskie"),
                Map.entry("SK", "Świętokrzyskie"), Map.entry("WN", "Warmińsko-mazurskie"),
                Map.entry("WP", "Wielkopolskie"), Map.entry("ZP", "Zachodniopomorskie")
        );

        List<WojewodztwoDB> listaWojewodztw = new ArrayList<>();
        for (Map.Entry<String, String> entry : woj.entrySet()) {
            WojewodztwoDB w = new WojewodztwoDB();
            w.setKodWoj(entry.getKey());
            w.setNazwaWoj(entry.getValue());
            listaWojewodztw.add(w);
        }
        return wojewodztwoRepository.saveAll(listaWojewodztw);
    }

    // 2. MIASTA
    private List<MiastoDB> ladujMiasta(List<WojewodztwoDB> wojewodztwa) {
        Map<String, List<String>> miastaMap = Map.of(
                "MZ", List.of("Warszawa", "Radom", "Płock"),
                "MA", List.of("Kraków", "Tarnów", "Nowy Sącz"),
                "SL", List.of("Katowice", "Częstochowa", "Gliwice"),
                "DS", List.of("Wrocław", "Wałbrzych"),
                "WP", List.of("Poznań", "Kalisz"),
                "PM", List.of("Gdańsk", "Gdynia", "Sopot"),
                "LU", List.of("Lublin", "Zamość"),
                "LD", List.of("Łódź", "Piotrków Trybunalski"),
                "ZP", List.of("Szczecin", "Koszalin")
        );

        List<MiastoDB> listaMiast = new ArrayList<>();
        Map<String, WojewodztwoDB> wojewodztwaMap = new HashMap<>();
        for(WojewodztwoDB w : wojewodztwa) {
            wojewodztwaMap.put(w.getKodWoj(), w);
        }

        for (Map.Entry<String, List<String>> entry : miastaMap.entrySet()) {
            WojewodztwoDB w = wojewodztwaMap.get(entry.getKey());
            if (w != null) {
                for (String nazwaMiasta : entry.getValue()) {
                    MiastoDB m = new MiastoDB();
                    m.setNazwaMiasta(nazwaMiasta);
                    m.setWojewodztwo(w);
                    listaMiast.add(m);
                }
            }
        }
        return miastoRepository.saveAll(listaMiast);
    }

    // 3. FIRMY
    private List<FirmaDB> ladujFirmy(List<MiastoDB> miasta, Faker faker) {
        List<String> nazwyFirm = List.of(
                "Google", "Samsung", "Accenture", "Microsoft", "Amazon", "Intel",
                "Nokia", "Capgemini", "Sii Polska", "Nordea", "Asseco",
                "Comarch", "CD Projekt Red", "Allegro", "NVIDIA"
        );

        List<FirmaDB> listaFirm = new ArrayList<>();
        for (String nazwa : nazwyFirm) {
            FirmaDB f = new FirmaDB();
            f.setNazwaFirmy(nazwa);
            f.setMiasto(wylosujElementZListy(miasta)); // Losowe miasto z listy
            f.setUlica(faker.address().streetName());
            f.setKodPocztowy(faker.address().zipCode());
            listaFirm.add(f);
        }
        return firmaRepository.saveAll(listaFirm);
    }

    // 4. TECHNOLOGIE
    private List<TechnologiaDB> ladujTechnologie() {
        List<String> nazwyTech = List.of(
                "Java", "Spring Boot", "Python", "Django", "JavaScript", "TypeScript",
                "Angular", "React", "Vue.js", "SQL", "PostgreSQL", "MongoDB",
                "Docker", "Kubernetes", "AWS", "Azure", "Git", "HelpDesk", "AutoCAD",
                "C#", ".NET", "Linux", "Scrum", "JIRA", "Figma"
        );

        List<TechnologiaDB> listaTech = new ArrayList<>();
        for (String nazwa : nazwyTech) {
            TechnologiaDB t = new TechnologiaDB();
            t.setNazwaTechnologii(nazwa);
            listaTech.add(t);
        }
        return technologiaRepository.saveAll(listaTech);
    }

    // 5. & 6. OFERTY PRACY + LISTY TECHNOLOGII
    private void ladujOfertyPracy(List<FirmaDB> firmy, List<TechnologiaDB> technologie, Faker faker) {
        List<String> poziomy = List.of("Młodszy Specjalista", "Specjalista", "Starszy Specjalista", "Ekspert", "Manager");
        List<OfertaPracyDB> listaOfert = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            OfertaPracyDB oferta = new OfertaPracyDB();

            String poziom = wylosujElementZListy(poziomy);
            String stanowisko = poziom + " ds. " + faker.job().field();

            oferta.setNazwaStanowiska(stanowisko);

            Widelki nowoWylosowaneWidelki = generujWidelki(poziom);

            oferta.setWidelkiMin(nowoWylosowaneWidelki.min());
            oferta.setWidelkiMax(nowoWylosowaneWidelki.max());

            oferta.setKrotkiOpis(faker.lorem().paragraph(2));
            oferta.setFirma(wylosujElementZListy(firmy));

            // 6. Losowanie 1-3 technologii
            int liczbaTech = ThreadLocalRandom.current().nextInt(1, 4); // Losuje 1, 2, lub 3
            Set<TechnologiaDB> wylosowaneTech = new HashSet<>();
            for (int j = 0; j < liczbaTech; j++) {
                wylosowaneTech.add(wylosujElementZListy(technologie));
            }
            oferta.setTechnologie(wylosowaneTech);

            listaOfert.add(oferta);
        }

        // Zapisanie ofert automatycznie zapisze relacje w tabeli LISTY_TECHNOLOGII
        ofertaPracyRepository.saveAll(listaOfert);
    }

    // --- Metody Pomocnicze ---

    // 1. Definiujemy nasz "Pair" jako prosty record.
    private record Widelki(int min, int max) {}

    // 2. Metoda generujWidelki zwraca teraz nasz record Widelki
    private Widelki generujWidelki(String poziom) {
        return switch (poziom) {
            case "Młodszy Specjalista" -> losujPensje(7000, 10000);
            case "Specjalista" -> losujPensje(10000, 15000);
            case "Starszy Specjalista" -> losujPensje(17000, 30000);
            case "Ekspert", "Manager" -> losujPensje(30000, 45000);
            default -> losujPensje(5000, 8000);
        };
    }

    // 3. Metoda losujPensje zwraca teraz nasz record Widelki, a nie String
    private Widelki losujPensje(int min, int max) {
        // Losuje pensje "do setek"
        int dol = ThreadLocalRandom.current().nextInt(min / 100, (max - 1000) / 100) * 100;
        int gora = ThreadLocalRandom.current().nextInt(dol / 100 + 10, max / 100) * 100;

        // Zwracamy obiekt Widelki zamiast Stringa
        return new Widelki(dol, gora);
    }

    // Ta metoda zostaje bez zmian
    private <T> T wylosujElementZListy(List<T> lista) {
        return lista.get(ThreadLocalRandom.current().nextInt(lista.size()));
    }
}
