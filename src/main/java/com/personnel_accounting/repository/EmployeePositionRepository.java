package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeePositionRepository extends JpaRepository<EmployeePosition, Long> {

    List<EmployeePosition> findByEmployee(Employee employee);
    List<EmployeePosition> findByPosition(Position position);
    List<EmployeePosition> findByProject(Project project);

    @Modifying
    @Query("update EmployeePosition e " +
            "set e.isActive = :isActive, e.modifiedDate = :date where e.id = :employeePositionId")
    int changeActiveStatus(@Param("employeePositionId") Long employeePositionId,
                               @Param("isActive") boolean isActive,
                               @Param("date") Date date);

    @Query("select e from EmployeePosition e where e.employee = :employee and " +
            "concat(e.id, e.employee.id, e.employee.name, e.employee.user.username, " +
            "e.employee.user.roles, e.isActive) like %:search%")
    Page<EmployeePosition> findByEmployee(@Param("employee") Employee employee,
                                          Pageable pageable,
                                          @Param("search") String search);
}