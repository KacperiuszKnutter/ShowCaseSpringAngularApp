package com.politechnika_warszawska.wyszukiwarkaprac.encje_db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "TECHNOLOGIE")
@Getter
@Setter
public class TechnologiaDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TECHNOLOGII")
    private Long idTechnologii;

    @Column(name = "NAZWA_TECHNOLOGII", nullable = false, unique = true)
    private String nazwaTechnologii;

    @ManyToMany(mappedBy = "technologie")
    private Set<OfertaPracyDB> ofertyPracy;
}
