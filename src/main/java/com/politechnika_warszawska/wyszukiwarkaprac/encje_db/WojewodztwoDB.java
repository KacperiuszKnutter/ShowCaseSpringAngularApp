package com.politechnika_warszawska.wyszukiwarkaprac.encje_db;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "WOJEWODZTWA")
@Getter
@Setter
public class WojewodztwoDB {

    @Id
    @Column(name = "KOD_WOJ", length = 3)// NP. 'MAZ'
    private String kodWoj;

    @Column(name = "NAZWA_WOJ", nullable = false)
    private String nazwaWoj;

    @OneToMany(mappedBy = "wojewodztwo")
    private Set<MiastoDB> miasta;
}
