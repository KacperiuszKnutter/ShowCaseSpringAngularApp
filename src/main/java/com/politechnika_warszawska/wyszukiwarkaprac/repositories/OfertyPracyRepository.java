package com.politechnika_warszawska.wyszukiwarkaprac.repositories;

import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.OfertaPracyDB;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OfertyPracyRepository  extends JpaRepository<OfertaPracyDB, Long>, JpaSpecificationExecutor<OfertaPracyDB> {
    Page<OfertaPracyDB> findByUlubionePrzezUzytkownikow_Email(String email, Pageable pageable);
}
