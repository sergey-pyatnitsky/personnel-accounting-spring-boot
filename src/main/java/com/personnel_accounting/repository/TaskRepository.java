package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.*;
import com.personnel_accounting.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProject(Project project);
    List<Task> findTasksByAssignee(Employee employee);

    @Query("select t from Task t where t.assignee = :employee and t.project = :project and t.taskStatus = :taskStatus and " +
            "concat(t.id, t.name, t.description, t.taskStatus) like %:search%")
    Page<Task> findTasksByAssigneeAndProjectAndTaskStatus(
            @Param("employee") Employee employee, @Param("project") Project project,
            @Param("taskStatus") TaskStatus taskStatus, Pageable pageable, @Param("search") String search);

    @Query("select t from Task t where t.project = :project and t.taskStatus = :taskStatus and " +
            "concat(t.id, t.name, t.description, t.taskStatus) like %:search%")
    Page<Task> findTasksByProjectAndTaskStatus(
            @Param("project") Project project, @Param("taskStatus") TaskStatus taskStatus,
            Pageable pageable, @Param("search") String search);

    @Query("select t from Task t where t.taskStatus = :taskStatus and " +
            "concat(t.id, t.name, t.description, t.taskStatus) like %:search%")
    Page<Task> findTasksByTaskStatus(
            @Param("taskStatus") TaskStatus taskStatus,
            Pageable pageable, @Param("search") String search);

    @Query("select t from Task t where t.project.department = :department and t.assignee = :employee and " +
            "t.taskStatus = :taskStatus and " +
            "concat(t.id, t.name, t.description, t.taskStatus) like %:search%")
    Page<Task> findTasksByAssigneeAndTaskStatusAndProject_Department(
            @Param("department") Department department, @Param("employee") Employee employee,
            @Param("taskStatus") TaskStatus taskStatus, Pageable pageable, @Param("search") String search);

    @Query("select t from Task t where t.project.department = :department and t.taskStatus = :taskStatus and " +
            "concat(t.id, t.name, t.description, t.taskStatus) like %:search%")
    Page<Task> findTasksByTaskStatusAndProject_Department(
            @Param("department") Department department, @Param("taskStatus") TaskStatus taskStatus,
            Pageable pageable, @Param("search") String search);

    @Query("select t from Task t where t.assignee = :employee and t.taskStatus = :taskStatus and " +
            "concat(t.id, t.name, t.description, t.taskStatus) like %:search%")
    Page<Task> findTasksByAssigneeAndTaskStatus(
            @Param("employee") Employee employee, @Param("taskStatus") TaskStatus taskStatus,
            Pageable pageable, @Param("search") String search);

    @Query("select t from Task t where t.assignee.user = :user and t.taskStatus = :taskStatus and " +
            "concat(t.id, t.name, t.description, t.taskStatus) like %:search%")
    Page<Task> findTasksByTaskStatusAndAndAssignee_User(
            @Param("user") User user, @Param("taskStatus") TaskStatus taskStatus,
            Pageable pageable, @Param("search") String search);

}
