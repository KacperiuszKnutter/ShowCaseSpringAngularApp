package com.politechnika_warszawska.wyszukiwarkaprac.encje_db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "MIASTA")
@Getter
@Setter
public class MiastoDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MIASTA")
    private long idMiasta;

    @Column(name = "NAZWA_MIASTA")
    private String nazwaMiasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KOD_WOJ", referencedColumnName = "KOD_WOJ")
    private WojewodztwoDB wojewodztwo;

    @OneToMany(mappedBy = "miasto")
    private Set<FirmaDB> firmy;

}