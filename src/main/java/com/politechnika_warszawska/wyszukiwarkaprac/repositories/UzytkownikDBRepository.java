package com.politechnika_warszawska.wyszukiwarkaprac.repositories;

import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.UzytkownikDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UzytkownikDBRepository extends JpaRepository<UzytkownikDB, Long> {
    Optional<UzytkownikDB> findByEmail(String email);
}
