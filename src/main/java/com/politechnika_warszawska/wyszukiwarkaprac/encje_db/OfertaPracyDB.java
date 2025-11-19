package com.politechnika_warszawska.wyszukiwarkaprac.encje_db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "OFERTY_PRACY")
@Getter
@Setter
public class OfertaPracyDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_OFERTY")
    private Long idOferty;

    @Column(name = "NAZWA_STANOWISKA", nullable = false)
    private String nazwaStanowiska;

    @Column(name = "WIDELKI_MIN")
    private Integer widelkiMin;

    @Column(name = "WIDELKI_MAX")
    private Integer widelkiMax;

    @Column(name = "KROTKI_OPIS", length = 250)
    private String krotkiOpis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FIRMY")
    private FirmaDB firma;

    // LISTY_TECHNOLOGII
    @ManyToMany
    @JoinTable(
            name = "LISTY_TECHNOLOGII",
            joinColumns = @JoinColumn(name = "ID_OFERTY"),
            inverseJoinColumns = @JoinColumn(name = "ID_TECHNOLOGII")
    )
    private Set<TechnologiaDB> technologie = new HashSet<>();

    @ManyToMany(mappedBy = "ofertyPracy")
    private Set<UzytkownikDB> ulubionePrzezUzytkownikow = new HashSet<>();
}