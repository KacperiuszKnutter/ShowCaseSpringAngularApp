package com.politechnika_warszawska.wyszukiwarkaprac.repositories;

import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.WojewodztwoDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WojewodztwoDBRepository extends JpaRepository<WojewodztwoDB, String> {
}
