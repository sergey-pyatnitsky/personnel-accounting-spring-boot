package com.personnel_accounting.service.project;

import com.personnel_accounting.entity.domain.*;
import com.personnel_accounting.entity.enums.TaskStatus;
import com.personnel_accounting.exception.ActiveStatusDataException;
import com.personnel_accounting.exception.ExistingDataException;
import com.personnel_accounting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeePositionRepository employeePositionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Project addProject(Project project, Long departmentId) {
        project.setCreateDate(new Date(System.currentTimeMillis()));
        project.setActive(false);
        List<Project> projects = projectRepository.findByName(project.getName())
                .stream().filter(obj -> obj.getEndDate() == null).collect(Collectors.toList());
        if (projects.size() != 0) {
            if(!departmentRepository.findById(departmentId).orElse(null).isActive())
                throw new ActiveStatusDataException("Данный отдел неактивен для добавление проектов!");

            if(projects.stream().allMatch(obj -> obj.getDepartment().getId().equals(departmentId)
                    && obj.getDepartment().isActive() && obj.isActive()))
                throw new ExistingDataException("Данный проект уже существует!");
        }
        return assignProjectToDepartmentId(project, departmentId);
    }

    @Override
    public boolean closeProject(Project project) {
        Project tempProject = projectRepository.findById(project.getId()).orElse(null);
        if (tempProject == null) return false;
        if (tempProject.getStartDate() == null)
            return projectRepository.removeById(tempProject.getId()) == 1;
        else {
            Date date = new Date(System.currentTimeMillis());
            List<EmployeePosition> employeePositions = employeePositionRepository.findByProject(tempProject);
            List<Task> tasks = taskRepository.findByProject(tempProject);
            if (employeePositions.size() == 0
                    || employeePositions.stream().noneMatch(employeePosition -> employeePosition.getEndDate() == null)) {
                if (tasks.size() == 0 || tasks.stream().noneMatch(task -> task.getTaskStatus() != TaskStatus.CLOSED)) {
                    tempProject.setEndDate(date);
                    tempProject.setModifiedDate(date);
                    projectRepository.save(tempProject);
                    return true;
                }
                return false;
            } else {
                employeePositions.forEach(employeePosition -> {
                    employeePosition.setEndDate(date);
                    employeePosition.setModifiedDate(date);
                    changeEmployeeStateInProject(employeePosition, false);
                });
            }
            return false;
        }
    }

    @Override
    public Project assignProjectToDepartmentId(Project project, Long departmentId) {
        List<Project> projects = projectRepository.findByName(project.getName())
                .stream().filter(obj -> obj.getEndDate() == null).collect(Collectors.toList());
        if (projects.size() == 0 || projects.stream().allMatch(obj ->
                !obj.getDepartment().getId().equals(departmentId) && obj.getDepartment().isActive())) {
            project.setDepartment(departmentRepository.findById(departmentId).orElse(null));
            return projectRepository.save(project);
        }
        return project;
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public boolean remove(Project project) {
        return projectRepository.removeById(project.getId()) == 1;
    }

    @Override
    public boolean inactivate(Project project) {
        project = projectRepository.findById(project.getId()).orElse(null);
        if (project == null) return false;
        employeePositionRepository.findByProject(project).forEach(employeePosition -> {
            employeePosition.setActive(true);
            employeePositionRepository.save(employeePosition);
        });
        return taskRepository.findByProject(project).stream().noneMatch(task -> task.getTaskStatus() != TaskStatus.CLOSED)
                && projectRepository.changeActiveStatus(project.getId(), false, new Date(System.currentTimeMillis())) == 1;
    }

    @Override
    public boolean activate(Project project) {
        project = projectRepository.findById(project.getId()).orElse(null);
        if (project == null) return false;
        employeePositionRepository.findByProject(project).forEach(employeePosition -> {
            employeePosition.setActive(true);
            employeePositionRepository.save(employeePosition);
        });
        project.setStartDate(new Date(System.currentTimeMillis()));
        projectRepository.save(project);
        return projectRepository.changeActiveStatus(project.getId(), true, new Date(System.currentTimeMillis())) == 1;
    }

    @Override
    public Project find(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Project> findAllOpenProjectsWithPaginationAndSearch(Pageable pageable, String search) {
        return projectRepository.findProjectsByEndDateIsNull(pageable, search);
    }

    @Override
    public Page<Project> findAllOpenProjectsByDepartmentWithPaginationAndSearch(
            Department department, Pageable pageable, String search) {
        return projectRepository.findProjectsByDepartmentAndEndDateIsNull(department, pageable, search);
    }

    @Override
    public Page<Project> findAllOpenProjectsByEmployeeWithPaginationAndSearch(
            Employee employee, Pageable pageable, String search) {
        return projectRepository.findAllOpenProjectByEmployee(employee, pageable, search);
    }

    @Override
    public Page<Project> findAllClosedProjectsWithPaginationAndSearch(Pageable pageable, String search) {
        return projectRepository.findProjectsByEndDateIsNotNull(pageable, search);
    }

    @Override
    public List<Project> findByDepartment(Department department) {
        return projectRepository.findByDepartment(department);
    }

    @Override
    public Page<Task> findTasksByTaskStatusWithPaginationAndSearch(TaskStatus taskStatus, Pageable pageable, String search) {
        return taskRepository.findTasksByTaskStatus(taskStatus, pageable, search);
    }

    @Override
    public Department findDepartmentByUser(User user) {
        return departmentRepository.findDepartmentByUser(user);
    }

    @Override
    public EmployeePosition changeEmployeePositionInProject(EmployeePosition employeePosition, Position position) {
        if (!employeePosition.getPosition().equals(position)) {
            position = positionRepository.findById(position.getId()).orElse(null);
            if (position != null) {
                employeePosition.setPosition(position);
                return employeePositionRepository.save(employeePosition);
            }
        }
        return employeePositionRepository.findById(employeePosition.getId()).orElse(null);
    }

    @Override
    public EmployeePosition assignToProject(Employee employee, Project project, Position position) {
        List<EmployeePosition> employeePositions = employeePositionRepository.findByEmployee(employee);

        for (EmployeePosition obj : employeePositions) {
            if (obj.getProject().getId().equals(project.getId()) && obj.getEndDate() == null)
                return employeePositionRepository.findById(obj.getId()).orElse(null);
        }
        EmployeePosition employeePosition = EmployeePosition.builder()
                .isActive(true).employee(employee).position(position)
                .project(project).department(project.getDepartment())
                .createDate(new Date(System.currentTimeMillis())).build();
        employeePosition.setStartDate(new Date(System.currentTimeMillis()));
        employee = employeeRepository.findById(employee.getId()).orElse(null);
        if(employee != null){
            employee.setActive(true);
            employeeRepository.save(employee);
        }
        return employeePositionRepository.save(employeePosition);
    }

    @Override
    public EmployeePosition changeEmployeeStateInProject(EmployeePosition employeePosition, boolean isActive) {
        if (employeePosition.isActive() != isActive) {
            employeePosition.setActive(isActive);
            Date date = new Date(System.currentTimeMillis());
            employeePosition.setModifiedDate(date);
            employeePosition.setEndDate(date);
            return employeePositionRepository.save(employeePosition);
        }
        return employeePositionRepository.findById(employeePosition.getId()).orElse(null);
    }

    @Override
    public List<Employee> findEmployeesByProjectInEmployeePosition(Project project) {
        return employeePositionRepository.findByProject(project).stream()
                .map(EmployeePosition::getEmployee)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeePosition> findProjectEmployeePositions(Employee employee, Project project) {
        return employeePositionRepository.findByProject(project).stream()
                .filter(employeePosition -> employeePosition.getEmployee().getId().equals(employee.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeePosition> findEmployeePositions(Employee employee) {
        return employeePositionRepository.findByEmployee(employee);
    }

    @Override
    public Page<EmployeePosition> findEmployeePositionsByEmployeeWithPaginationAndSearch(
            Employee employee, Pageable pageable, String search) {
        return employeePositionRepository.findByEmployee(employee, pageable, search);
    }

    @Override
    public Page<Task> findTasksByAssigneeProjectAndTaskStatusWithPaginationAndSearch(
            Employee employee, Project project, TaskStatus taskStatus, Pageable pageable, String search) {
        return taskRepository.findTasksByAssigneeAndProjectAndTaskStatus(employee, project, taskStatus, pageable, search);
    }

    @Override
    public Page<Task> findTasksInProjectByStatusWithPaginationAndSearch(
            Project project, TaskStatus taskStatus, Pageable pageable, String search) {
        return taskRepository.findTasksByProjectAndTaskStatus(project, taskStatus, pageable, search);
    }

    @Override
    public Page<Task> findTaskByDepartmentTaskStatusAndEmployeeWithPaginationAndSearch(
            Department department, Employee employee, TaskStatus taskStatus, Pageable pageable, String search) {
        return taskRepository.findTasksByAssigneeAndTaskStatusAndProject_Department(
                department, employee, taskStatus, pageable, search);
    }

    @Override
    public Page<Task> findTasksByDepartmentAndTaskStatusWithPaginationAndSearch(
            Department department, TaskStatus taskStatus, Pageable pageable, String search) {
        return taskRepository.findTasksByTaskStatusAndProject_Department(department, taskStatus, pageable, search);
    }

    @Override
    public Page<Task> findTasksByTaskStatusAndEmployeeWithPaginationAndSearch(
            Employee employee, TaskStatus taskStatus, Pageable pageable, String search) {
        return taskRepository.findTasksByAssigneeAndTaskStatus(employee, taskStatus, pageable, search);
    }

    @Override
    public Page<Task> findTaskByUserAndTaskStatusWithPaginationAndSearch(
            User user, TaskStatus taskStatus, Pageable pageable, String search) {
        return taskRepository.findTasksByTaskStatusAndAndAssignee_User(user, taskStatus, pageable, search);
    }
}
