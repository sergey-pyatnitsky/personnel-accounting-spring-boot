package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.ReportCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportCardRepository extends JpaRepository<ReportCard, Long> {
}
