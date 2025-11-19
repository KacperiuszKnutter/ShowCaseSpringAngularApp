package com.politechnika_warszawska.wyszukiwarkaprac.encje_db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "UZYTKOWNICY")
@Getter
@Setter
public class UzytkownikDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UZYTKOWNIKA")
    private Long idUzytkownika;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    // lista ulubionych ofert pracy danego uzytkownika
    @ManyToMany
    @JoinTable(
            name = "ULUBIONE_OFERTY",
            joinColumns = @JoinColumn(name = "ID_UZYTKOWNIKA"),
            inverseJoinColumns = @JoinColumn(name = "ID_OFERTY")
    )
    private Set<OfertaPracyDB> ofertyPracy = new HashSet<>();


}
