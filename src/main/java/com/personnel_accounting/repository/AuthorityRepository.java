package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    @Query
    boolean removeByName(String name);

    @Query
    Optional<Authority> findByName(String name);
}