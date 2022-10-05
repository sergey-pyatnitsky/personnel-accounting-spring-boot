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
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Modifying
    @Query("update Employee e set e.isActive =:isActive, e.modifiedDate =:date where e.id =:employeeId")
    int changeActiveStatus(@Param("employeeId") Long employeeId,
                           @Param("isActive") boolean isActive,
                           @Param("date") Date date);

    boolean removeById(Long id);

    List<Employee> findByDepartment(Department department);

    Employee findByProfile(Profile profile);

    Employee findByUser(User user);

    List<Employee> findEmployeesByNameContaining(String name);

    @Query("select e from Employee e " +
            "where concat(e.id, e.user.username, e.name, e.isActive, e.user.isActive) like %:search%")
    Page<Employee> findAll(Pageable pageable, @Param("search") String search);

    @Query("select e from Employee e " +
            "where e.department is null and e.user.isActive = true and " +
            "concat(e.id, e.user.username, e.name, e.isActive, e.user.isActive) like %:search%")
    Page<Employee> findEmployeesByDepartmentIsNullAndUser_ActiveIsTrue(Pageable pageable, @Param("search") String search);

    @Query("select e from Employee e " +
            "where e.department is not null and e.user.isActive = true and " +
            "concat(e.id, e.user.username, e.name, e.isActive, e.user.isActive) like %:search%")
    Page<Employee> findEmployeesByDepartmentIsNotNullAndUser_ActiveIsTrue(Pageable pageable, @Param("search") String search);

    @Query("select e from Employee e " +
            "where exists ( select ep from EmployeePosition ep where ep.employee = e and ep.project is not null " +
            "and ep.endDate is null ) " +
            "and e.department = :department " +
            "and concat(e.id, e.user.username, e.name, e.isActive, e.user.isActive) like %:search%")
    Page<Employee> findEmployeesByDepartmentWithOpenProjects(
            @Param("department") Department department,
            Pageable pageable,
            @Param("search") String search);

    @Query("select e from Employee e " +
            "where e.department = :department " +
            "and concat(e.id, e.user.username, e.name, e.isActive, e.user.isActive) like %:search%")
    Page<Employee> findEmployeesByDepartment(
            @Param("department") Department department,
            Pageable pageable, @Param("search") String search);

    @Query("select e.employee from EmployeePosition e " +
            "where e.project = :project " +
            "and concat(e.employee.id, e.employee.user.username, e.employee.name, " +
            "e.employee.isActive, e.employee.user.isActive) like %:search%")
    Page<Employee> findEmployeesByProject(
            @Param("project") Project project,
            Pageable pageable, @Param("search") String search);

    @Query("select e from Employee e join e.user.roles roles " +
            "where roles in (:authorities) and e.user.isActive = false " +
            "and concat(e.id, e.user.username, e.name, e.isActive, e.user.isActive) like %:search%")
    Page<Employee> findByUser_IsActiveFalseAndUser_RolesIn(
            @Param("authorities") List<Authority> first,
            Pageable pageable, @Param("search") String search);

    @Query("select e from Employee e join e.user.roles roles " +
            "where roles in (:authorities) and e.user.isActive = true" +
            " and concat(e.id, e.user.username, e.name, e.isActive, e.user.isActive) like %:search%")
    Page<Employee> findEmployeesByUser_ActiveIsTrueAndUser_RolesIn(
            @Param("authorities") List<Authority> authorities,
            Pageable pageable, @Param("search") String search);
}