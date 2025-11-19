package com.politechnika_warszawska.wyszukiwarkaprac;

import com.politechnika_warszawska.wyszukiwarkaprac.services.OfertaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

// Nasze Encje i DTO
import com.politechnika_warszawska.wyszukiwarkaprac.dtobjects.OfertaListDTO;
import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.FirmaDB;
import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.MiastoDB;
import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.OfertaPracyDB;
import com.politechnika_warszawska.wyszukiwarkaprac.repositories.OfertyPracyRepository;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Aktywuje Mockito
class OfertaServiceTest {

    @Mock // pusta wersja repozytorium
    private OfertyPracyRepository ofertyPracyRepository;

    @InjectMocks // wstrzyknij do ofertaServiceto repo
    private OfertaService ofertaService;

    // === TEST 1: Wywołanie bez filtrów zwraca pełną stronę ===
    @Test
    void gdySzukajOfertBezFiltrow_powinnoZwrocicStrone20Elementow() {
        // ARRANGE (Przygotuj)

        // 1. Stwórz obiekt żądania strony (dokładnie taki, jaki stworzy kontroler)
        Pageable pageable = PageRequest.of(0, 20); // Strona 0, rozmiar 20

        // 2. Stwórz listę fałszywych ofert (tyle, ile ma być na stronie)
        List<OfertaPracyDB> listaOfertEncji = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            // Musimy stworzyć minimalne obiekty, aby mapujDoDTO() nie wywalił błędu
            MiastoDB miasto = new MiastoDB();
            miasto.setNazwaMiasta("Testowe Miasto " + i);
            FirmaDB firma = new FirmaDB();
            firma.setNazwaFirmy("Testowa Firma " + i);
            firma.setMiasto(miasto);

            OfertaPracyDB oferta = new OfertaPracyDB();
            oferta.setIdOferty((long) i);
            oferta.setNazwaStanowiska("Tester " + i);
            oferta.setWidelkiMin(5000 + i);
            oferta.setWidelkiMax(8000 + i);
            oferta.setFirma(firma);
            listaOfertEncji.add(oferta);
        }

        // 3. Stwórz fałszywą "Stronę" (Page), którą ma zwrócić repozytorium
        // Mówimy: "strona zawiera 20 ofert, ale łącznie jest ich 100 (czyli 5 stron)"
        Page<OfertaPracyDB> mockPage = new PageImpl<>(listaOfertEncji, pageable, 100);

        // 4. Zaprogramuj Mocka: "Kiedy ktoś zawoła findAll z JAKĄKOLWIEK specyfikacją
        //    i DOKŁADNIE tym obiektem pageable, ZWRÓĆ naszą fałszywą stronę (mockPage)"
        when(ofertyPracyRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

        // ACT (Działaj)
        // Wywołaj metodę, którą testujemy, z pustymi filtrami
        Page<OfertaListDTO> wynik = ofertaService.szukajOfert(pageable, null, null, null, null, null, null);

        // ASSERT (Sprawdź)
        assertNotNull(wynik); // Czy wynik nie jest nullem?
        assertEquals(20, wynik.getNumberOfElements()); // Czy na stronie jest 20 elementów?
        assertEquals(100, wynik.getTotalElements()); // Czy łącznie jest 100 elementów?
        assertEquals(5, wynik.getTotalPages()); // Czy łącznie jest 5 stron?
        assertEquals(0, wynik.getNumber()); // Czy to jest strona numer 0?
        assertEquals("Tester 0", wynik.getContent().get(0).getNazwaStanowiska()); // Czy mapowanie DTO zadziałało?
        assertEquals("Testowe Miasto 19", wynik.getContent().get(19).getNazwaMiasta()); // Czy mapowanie DTO zadziałało?
    }

    // === TEST 2: Wywołanie z filtrem miasta zwraca wyniki ===
    @Test
    void gdySzukajOfertZFiltremMiasta_powinnoZwrocicTylkoPasujace() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 20);
        String nazwaMiasta = "Warszawa";

        // 1. Stwórz fałszywe dane - tym razem tylko JEDEN pasujący element
        MiastoDB miasto = new MiastoDB();
        miasto.setNazwaMiasta("Warszawa");
        FirmaDB firma = new FirmaDB();
        firma.setNazwaFirmy("TestCorp");
        firma.setMiasto(miasto);

        OfertaPracyDB ofertaWaw = new OfertaPracyDB();
        ofertaWaw.setIdOferty(1L);
        ofertaWaw.setNazwaStanowiska("Java Developer");
        ofertaWaw.setWidelkiMin(10000);
        ofertaWaw.setWidelkiMax(15000);
        ofertaWaw.setFirma(firma);

        // 2. Stwórz fałszywą stronę (tym razem ma tylko 1 element)
        Page<OfertaPracyDB> mockPage = new PageImpl<>(List.of(ofertaWaw), pageable, 1);

        // 3. Zaprogramuj Mocka (tak samo jak wcześniej)
        when(ofertyPracyRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

        // ACT
        // Wywołaj metodę, ale tym razem podaj filtr "nazwaMiasta"
        Page<OfertaListDTO> wynik = ofertaService.szukajOfert(pageable, null, nazwaMiasta, null, null, null, null);

        // ASSERT
        assertNotNull(wynik);
        assertEquals(1, wynik.getNumberOfElements()); // Sprawdź, czy jest tylko 1 wynik
        assertEquals(1, wynik.getTotalElements());
        assertEquals("Java Developer", wynik.getContent().get(0).getNazwaStanowiska());
        assertEquals("Warszawa", wynik.getContent().get(0).getNazwaMiasta()); // Najważniejsze: sprawdź, czy miasto się zgadza
    }
}
