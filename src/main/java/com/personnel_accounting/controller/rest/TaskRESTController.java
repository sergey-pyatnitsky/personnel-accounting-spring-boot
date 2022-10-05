package com.personnel_accounting.controller.rest;

import com.personnel_accounting.entity.domain.*;
import com.personnel_accounting.entity.dto.TaskDTO;
import com.personnel_accounting.entity.enums.TaskStatus;
import com.personnel_accounting.entity.pagination.DataTablePagingRequest;
import com.personnel_accounting.mapper.DepartmentMapper;
import com.personnel_accounting.mapper.TaskMapper;
import com.personnel_accounting.service.department.DepartmentService;
import com.personnel_accounting.service.employee.EmployeeService;
import com.personnel_accounting.service.project.ProjectService;
import com.personnel_accounting.service.user.UserService;
import com.personnel_accounting.utils.AuthenticationUtil;
import com.personnel_accounting.utils.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Time;

@RestController
@RequestMapping("api/task")
public class TaskRESTController {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/add")
    public ResponseEntity<?> addTask(@Valid @RequestBody TaskDTO taskDTO, Authentication authentication) {
        Task task = taskMapper.toModal(taskDTO);
        task.setAssignee(employeeService.find(taskDTO.getAssignee().getId()));
        task.setReporter(employeeService.findByUser(
                userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))));
        return ResponseEntity.ok(
                taskMapper.toDto(
                        employeeService.addTaskInProject(
                                projectService.find(taskDTO.getProject().getId()), task
                        )
                )
        );
    }

    @PostMapping("/get_all/by_status/{status}")
    public ResponseEntity<?> getAllTaskByStatusPaginated(@RequestBody DataTablePagingRequest pagingRequest,
                                                         @PathVariable TaskStatus status, Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication));
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findTaskByUserAndTaskStatusWithPaginationAndSearch(
                                user, status,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(task -> {
                            TaskDTO taskDTO = taskMapper.toDto(task);
                            taskDTO.setDepartment(departmentMapper.toDto(task.getProject().getDepartment()));
                            return taskDTO;
                        }), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/get_all/by_status/{status}/employee")
    public ResponseEntity<?> getAllTaskByStatusAndEmployeePaginated(@RequestBody DataTablePagingRequest pagingRequest,
                                                                    @PathVariable TaskStatus status, Authentication authentication) {
        Employee employee = employeeService.findByUser(
                userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication)));
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findTasksByTaskStatusAndEmployeeWithPaginationAndSearch(
                                employee, status,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(task -> {
                            TaskDTO taskDTO = taskMapper.toDto(task);
                            taskDTO.setDepartment(departmentMapper.toDto(task.getProject().getDepartment()));
                            return taskDTO;
                        }), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/get_all/by_department/{id}/by_status/{status}")
    public ResponseEntity<?> getAllTaskInDepartmentsByStatusPaginated(@RequestBody DataTablePagingRequest pagingRequest,
                                                                      @PathVariable Long id, @PathVariable TaskStatus status) {
        Department department = departmentService.find(id);
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findTasksByDepartmentAndTaskStatusWithPaginationAndSearch(
                                department, status,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(task -> {
                            TaskDTO taskDTO = taskMapper.toDto(task);
                            taskDTO.setDepartment(departmentMapper.toDto(task.getProject().getDepartment()));
                            return taskDTO;
                        }), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/get_all/by_department/{id}/by_status/{status}/employee")
    public ResponseEntity<?> getAllTaskInDepartmentsByStatusAndEmployeePaginated(
            @RequestBody DataTablePagingRequest pagingRequest,
            @PathVariable Long id, @PathVariable TaskStatus status, Authentication authentication) {

        Department department = departmentService.find(id);
        Employee employee = employeeService.findByUser(
                userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication)));
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findTaskByDepartmentTaskStatusAndEmployeeWithPaginationAndSearch(
                                department, employee, status,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(task -> {
                            TaskDTO taskDTO = taskMapper.toDto(task);
                            taskDTO.setDepartment(departmentMapper.toDto(task.getProject().getDepartment()));
                            return taskDTO;
                        }), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/get_all/project/{id}/by_status/{status}")
    public ResponseEntity<?> getAllTasksInProjectByStatusPaginated(@RequestBody DataTablePagingRequest pagingRequest,
                                                                   @PathVariable TaskStatus status, @PathVariable Long id) {
        Project project = projectService.find(id);
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findTasksInProjectByStatusWithPaginationAndSearch(
                                project, status,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(task -> {
                            TaskDTO taskDTO = taskMapper.toDto(task);
                            taskDTO.setDepartment(departmentMapper.toDto(task.getProject().getDepartment()));
                            return taskDTO;
                        }), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/get_all/project/{id}/by_status/{status}/employee")
    public ResponseEntity<?> getAllTasksByEmployeeInProjectWithStatusPaginated(
            @RequestBody DataTablePagingRequest pagingRequest,
            @PathVariable TaskStatus status, @PathVariable Long id, Authentication authentication) {

        Project project = projectService.find(id);
        Employee employee = employeeService.findByUser(
                userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication)));
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findTasksByAssigneeProjectAndTaskStatusWithPaginationAndSearch(
                                employee, project, status,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(task -> {
                            TaskDTO taskDTO = taskMapper.toDto(task);
                            taskDTO.setDepartment(departmentMapper.toDto(task.getProject().getDepartment()));
                            return taskDTO;
                        }), pagingRequest.getDraw()
                )
        );
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        Task task = employeeService.findTask(id);
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setTaskStatus(taskDTO.getStatus());
        employeeService.saveTask(task);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit/status/{id}")
    public ResponseEntity<?> editTaskStatus(@PathVariable Long id, @RequestBody String time) {
        Task task = employeeService.findTask(id);
        if(!task.getTaskStatus().equals(TaskStatus.CLOSED))employeeService.changeTaskStatus(task);
        if (!time.equals("null"))
            employeeService.trackTime(task, Time.valueOf(time.replaceAll("\"", "") + ":00"));
        return ResponseEntity.ok().build();
    }
}
