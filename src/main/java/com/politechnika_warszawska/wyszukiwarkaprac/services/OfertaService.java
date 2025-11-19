package com.politechnika_warszawska.wyszukiwarkaprac.services;

import com.politechnika_warszawska.wyszukiwarkaprac.dtobjects.OfertaListDTO;

import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.OfertaPracyDB;
import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.TechnologiaDB;
import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.UzytkownikDB;
import com.politechnika_warszawska.wyszukiwarkaprac.repositories.OfertyPracyRepository;
import com.politechnika_warszawska.wyszukiwarkaprac.repositories.UzytkownikDBRepository;
import com.politechnika_warszawska.wyszukiwarkaprac.services.OfertaSpecification; // Upewnij się co do pakietu
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfertaService {

    private final OfertyPracyRepository ofertaPracyRepository;
    private final UzytkownikDBRepository uzytkownikRepository;

    public OfertaService(OfertyPracyRepository ofertaPracyRepository, UzytkownikDBRepository uzytkownikRepository) {
        this.ofertaPracyRepository = ofertaPracyRepository;
        this.uzytkownikRepository = uzytkownikRepository;
    }

    // --- 1. WYSZUKIWANIE (LISTA) ---
    @Transactional(readOnly = true)
    public Page<OfertaListDTO> szukajOfert(Pageable pageable, String kodWoj, String nazwaMiasta, String nazwaFirmy, Integer minWidelki, Integer maxWidelki, String userEmail) {

        Specification<OfertaPracyDB> spec = (root, query, cb) -> cb.conjunction();

        if (kodWoj != null && !kodWoj.isEmpty()) spec = spec.and(OfertaSpecification.posiadaWybraneWojewodztwo(kodWoj));
        if (nazwaMiasta != null && !nazwaMiasta.isEmpty()) spec = spec.and(OfertaSpecification.posiadaWybraneMiasto(nazwaMiasta));
        if (nazwaFirmy != null && !nazwaFirmy.isEmpty()) spec = spec.and(OfertaSpecification.posiadaWybranaFirme(nazwaFirmy));
        if (minWidelki != null) spec = spec.and(OfertaSpecification.maMinimalneWidelki(minWidelki));
        if (maxWidelki != null) spec = spec.and(OfertaSpecification.maMaksymalneWidelki(maxWidelki));

        Page<OfertaPracyDB> stronaEncji = ofertaPracyRepository.findAll(spec, pageable);

        // Przekazujemy email, żeby sprawdzić czy oferta jest polubiona
        return stronaEncji.map(e -> mapujDoListDTO(e, userEmail));
    }

    // --- 2. POBIERANIE ULUBIONYCH ---
    @Transactional(readOnly = true)
    public Page<OfertaListDTO> pobierzUlubione(String emailUzytkownika, Pageable pageable) {
        Page<OfertaPracyDB> stronaEncji = ofertaPracyRepository.findByUlubionePrzezUzytkownikow_Email(emailUzytkownika, pageable);
        // Tutaj userEmail jest znany, więc isLiked zawsze będzie true
        return stronaEncji.map(e -> mapujDoListDTO(e, emailUzytkownika));
    }

    // --- 3. POBIERANIE SZCZEGÓŁÓW ---
    @Transactional(readOnly = true)
    public OfertaListDTO pobierzSzczegolyOferty(Long id, String userEmail) {
        OfertaPracyDB encja = ofertaPracyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono oferty o ID: " + id));

        return mapujDoDetailsDTO(encja, userEmail);
    }

    // --- 4. TOGGLE ULUBIONE (DODAJ/USUŃ) ---
    @Transactional
    public boolean zmienStatusUlubionej(Long idOferty, String email) {
        UzytkownikDB user = uzytkownikRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        OfertaPracyDB oferta = ofertaPracyRepository.findById(idOferty)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono oferty"));

        if (user.getOfertyPracy().contains(oferta)) {
            user.getOfertyPracy().remove(oferta);
            return false; // Usunięto
        } else {
            user.getOfertyPracy().add(oferta);
            return true; // Dodano
        }
    }

    // --- MAPERY ---

    private OfertaListDTO mapujDoListDTO(OfertaPracyDB encja, String userEmail) {
        OfertaListDTO dto = new OfertaListDTO();
        dto.setId(encja.getIdOferty());
        dto.setNazwaStanowiska(encja.getNazwaStanowiska());
        dto.setWidelkiMin(encja.getWidelkiMin());
        dto.setWidelkiMax(encja.getWidelkiMax());

        if (encja.getFirma() != null) {
            dto.setNazwaFirmy(encja.getFirma().getNazwaFirmy());
            if (encja.getFirma().getMiasto() != null) {
                dto.setNazwaMiasta(encja.getFirma().getMiasto().getNazwaMiasta());
            }
        }
        // Sprawdzanie czy user lubi ofertę
        dto.setLiked(checkIfLiked(encja, userEmail));
        return dto;
    }

    private OfertaListDTO mapujDoDetailsDTO(OfertaPracyDB encja, String userEmail) {
        OfertaListDTO dto = new OfertaListDTO();
        dto.setId(encja.getIdOferty());
        dto.setNazwaStanowiska(encja.getNazwaStanowiska());
        dto.setWidelkiMin(encja.getWidelkiMin());
        dto.setWidelkiMax(encja.getWidelkiMax());
        dto.setKrotkiOpis(encja.getKrotkiOpis());

        if (encja.getFirma() != null) {
            dto.setNazwaFirmy(encja.getFirma().getNazwaFirmy());
            if (encja.getFirma().getMiasto() != null) {
                dto.setNazwaMiasta(encja.getFirma().getMiasto().getNazwaMiasta());
            }
        }

        if (encja.getTechnologie() != null) {
            List<String> technologie = encja.getTechnologie().stream()
                    .map(TechnologiaDB::getNazwaTechnologii)
                    .collect(Collectors.toList());
            dto.setTechnologie(technologie);
        }

        dto.setLiked(checkIfLiked(encja, userEmail));
        return dto;
    }

    private boolean checkIfLiked(OfertaPracyDB encja, String userEmail) {
        if (userEmail == null) return false;
        return encja.getUlubionePrzezUzytkownikow().stream()
                .anyMatch(u -> u.getEmail().equals(userEmail));
    }
}