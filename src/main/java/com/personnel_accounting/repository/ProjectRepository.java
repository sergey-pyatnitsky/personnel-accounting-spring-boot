package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.Department;
import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.domain.Project;
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
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByDepartment(Department department);

    List<Project> findByName(String name);

    int removeById(Long id);

    @Modifying
    @Query("update Project p set p.isActive = :isActive, p.modifiedDate = :date where p.id = :projectId")
    int changeActiveStatus(@Param("projectId") Long projectId,
                               @Param("isActive") boolean isActive,
                               @Param("date") Date date);

    @Query("select p from Project p " +
            "where p.department = :department and " +
            "concat(p.id, p.name, p.department.id, p.department.name, p.isActive) like %:search% ")
    Page<Project> findProjectsByDepartmentAndEndDateIsNull(@Param("department") Department department,
                                                           Pageable pageable,
                                                           @Param("search") String search);

    @Query("select e.project from EmployeePosition e " +
            "where e.employee = :employee and e.endDate is null " +
            "and concat(e.project.id, e.project.name, e.project.department.id, " +
            "e.project.department.name, e.project.isActive) like %:search% ")
    Page<Project> findAllOpenProjectByEmployee(@Param("employee") Employee employee,
                                               Pageable pageable,
                                               @Param("search") String search);

    @Query("select p from Project p " +
            "where p.endDate is null and " +
            "concat(p.id, p.name, p.department.id, p.department.name, p.isActive) like %:search%")
    Page<Project> findProjectsByEndDateIsNull(Pageable pageable, @Param("search") String search);

    @Query("select p from Project p " +
            "where p.endDate is not null and " +
            "concat(p.id, p.name, p.department.id, p.department.name, p.isActive) like %:search%")
    Page<Project> findProjectsByEndDateIsNotNull(Pageable pageable, @Param("search") String search);

}
