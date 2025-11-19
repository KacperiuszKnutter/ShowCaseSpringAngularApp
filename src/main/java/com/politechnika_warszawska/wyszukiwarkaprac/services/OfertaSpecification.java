package com.politechnika_warszawska.wyszukiwarkaprac.services;

import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.*;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class OfertaSpecification {


    //odpowiedzialne za zapytania dla Where firma.idFirmy like/ = ? ""
    public static Specification<OfertaPracyDB> posiadaWybranaFirme(String nazwaFirmy) {
        return (root, query, cb) -> {
            Join<OfertaPracyDB, FirmaDB> firmaJoin = root.join("firma");
            // "like"  dla elastycznego wyszukiwania
            // "lower" aby wyszukiwanie nie było czułe na wielkość liter
            return cb.like(cb.lower(firmaJoin.get("nazwaFirmy")), "%" + nazwaFirmy.toLowerCase() + "%");
        };
    }

    // sprawdzamy oferty ktore maja dana firme i potem czy te firmy maja to miasto ktore jest w filtrze
    // where miast.idMiasta like ...
    public static Specification<OfertaPracyDB> posiadaWybraneMiasto(String nazwaMiasta) {
        return (root, query, criteriaBuilder) -> {
            Join<OfertaPracyDB, FirmaDB> firmaJoin = root.join("firma");
            Join<FirmaDB, MiastoDB> miastoJoin = firmaJoin.join("miasto");
            return criteriaBuilder.like(criteriaBuilder.lower(miastoJoin.get("nazwaMiasta")), "%" + nazwaMiasta.toLowerCase() + "%");
        };
    }

    public static Specification<OfertaPracyDB> posiadaWybraneWojewodztwo(String kodWoj) {
        return (root, query, criteriaBuilder) -> {
            Join<OfertaPracyDB, FirmaDB> firmaJoin = root.join("firma");
            Join<FirmaDB, MiastoDB> miastoJoin = firmaJoin.join("miasto");
            Join<MiastoDB, WojewodztwoDB> wojJoin = miastoJoin.join("wojewodztwo");
            return criteriaBuilder.equal(wojJoin.get("kodWoj"), kodWoj);
        };
    }

    // zapytania dla where widelkiMin >= ...
    public static Specification<OfertaPracyDB> maMinimalneWidelki(int min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("widelkiMin"), min);
    }
    // zapytania dla where widelkiMax <=
    public static Specification<OfertaPracyDB> maMaksymalneWidelki(int max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("widelkiMax"), max);
    }
}