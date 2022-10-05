package com.personnel_accounting.service.project;

import com.personnel_accounting.entity.domain.*;
import com.personnel_accounting.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    Project addProject(Project project, Long departmentId);
    boolean closeProject(Project project);
    Project assignProjectToDepartmentId(Project project, Long departmentId);

    Project save(Project project);
    boolean remove(Project project);
    boolean inactivate(Project project);
    boolean activate(Project project);
    Project find(Long id);

    Page<Project> findAllOpenProjectsWithPaginationAndSearch(Pageable pageable, String search);
    Page<Project> findAllOpenProjectsByDepartmentWithPaginationAndSearch(
            Department department, Pageable pageable, String search);
    Page<Project> findAllOpenProjectsByEmployeeWithPaginationAndSearch(
            Employee employee, Pageable pageable, String search);
    Page<Project> findAllClosedProjectsWithPaginationAndSearch(Pageable pageable, String search);
    List<Project> findByDepartment(Department department);
    Department findDepartmentByUser(User user);

    EmployeePosition changeEmployeePositionInProject(EmployeePosition employeePosition, Position position);
    EmployeePosition assignToProject(Employee employee, Project project, Position position);
    EmployeePosition changeEmployeeStateInProject(EmployeePosition employeePosition, boolean isActive);

    List<Employee> findEmployeesByProjectInEmployeePosition(Project project);
    List<EmployeePosition> findProjectEmployeePositions(Employee employee, Project project);
    List<EmployeePosition> findEmployeePositions(Employee employee);
    Page<EmployeePosition> findEmployeePositionsByEmployeeWithPaginationAndSearch(
            Employee employee, Pageable pageable, String search);

    Page<Task> findTasksByAssigneeProjectAndTaskStatusWithPaginationAndSearch(
            Employee employee, Project project, TaskStatus taskStatus, Pageable pageable, String search);
    Page<Task> findTasksInProjectByStatusWithPaginationAndSearch(
            Project project, TaskStatus taskStatus, Pageable pageable, String search);
    Page<Task> findTasksByTaskStatusWithPaginationAndSearch(
            TaskStatus taskStatus, Pageable pageable, String search);
    Page<Task> findTaskByDepartmentTaskStatusAndEmployeeWithPaginationAndSearch(
            Department department, Employee employee, TaskStatus taskStatus, Pageable pageable, String search);
    Page<Task> findTasksByDepartmentAndTaskStatusWithPaginationAndSearch(
            Department department, TaskStatus taskStatus, Pageable pageable, String search);
    Page<Task> findTasksByTaskStatusAndEmployeeWithPaginationAndSearch(
            Employee employee, TaskStatus taskStatus, Pageable pageable, String search);
    Page<Task> findTaskByUserAndTaskStatusWithPaginationAndSearch(
            User user, TaskStatus taskStatus, Pageable pageable, String search);
}
