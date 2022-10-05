package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Position findByName(String name);
    int removeById(Long id);

    @Query("select p from Position p where concat(p.id, p.name) like %:search%")
    Page<Position> findAll(Pageable pageable, @Param("search") String search);
}