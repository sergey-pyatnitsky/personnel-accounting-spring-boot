package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.Department;
import com.personnel_accounting.entity.domain.User;
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
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findDepartmentByName(String name);

    @Query("select e.department from Employee e where e.user = :user")
    Department findDepartmentByUser(@Param("user") User user);

    @Modifying
    @Query("update Department d set d.isActive =:isActive, d.modifiedDate =:date where d.id =:departmentId")
    int changeActiveStatus(@Param("departmentId") Long departmentId,
                               @Param("isActive") boolean isActive,
                               @Param("date") Date date);

    int removeById(Long id);

    @Query("select d from Department d where d.endDate is not null and concat(d.id, d.name, d.isActive) like %:search%")
    Page<Department> findDepartmentsByEndDateIsNotNull(Pageable pageable, @Param("search") String search);

    @Query("select d from Department d where d.endDate is null and concat(d.id, d.name, d.isActive) like %:search%")
    Page<Department> findDepartmentsByEndDateIsNull(Pageable pageable, @Param("search") String search);
}