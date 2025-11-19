package com.politechnika_warszawska.wyszukiwarkaprac.encje_db;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "FIRMY")
@Getter
@Setter
public class FirmaDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALIAS_FIRMY") // Użyjmy ID, a nie aliasu, jako klucza głównego
    private Long idFirmy;

    @Column(name = "NAZWA_FIRMY", nullable = false, unique = true)
    private String nazwaFirmy;

    @Column(name = "KOD_POCZTOWY", nullable = false, unique = true)
    private String kodPocztowy;

    @Column(name = "ULICA", nullable = false, unique = false)
    private String ulica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MIASTA")
    private MiastoDB miasto;

    @OneToMany(mappedBy = "firma")
    private Set<OfertaPracyDB> ofertyPracy;


}