package com.personnel_accounting.controller.rest;

import com.personnel_accounting.entity.domain.*;
import com.personnel_accounting.entity.dto.EmployeePositionDTO;
import com.personnel_accounting.entity.dto.ProjectDTO;
import com.personnel_accounting.entity.enums.Role;
import com.personnel_accounting.entity.pagination.DataTablePagingRequest;
import com.personnel_accounting.exception.ActiveStatusDataException;
import com.personnel_accounting.exception.ExistingDataException;
import com.personnel_accounting.exception.OperationExecutionException;
import com.personnel_accounting.mapper.ProjectMapper;
import com.personnel_accounting.service.department.DepartmentService;
import com.personnel_accounting.service.employee.EmployeeService;
import com.personnel_accounting.service.project.ProjectService;
import com.personnel_accounting.service.user.UserService;
import com.personnel_accounting.utils.AuthenticationUtil;
import com.personnel_accounting.utils.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.Objects;

@RestController
@RequestMapping("api/project")
public class ProjectRESTController {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/add")
    public ResponseEntity<?> addProject(@Valid @RequestBody ProjectDTO projectDTO, Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication));
        if (userService.getMaxRole(user) == Role.DEPARTMENT_HEAD)
            projectDTO.getDepartment().setId(projectService.findDepartmentByUser(user).getId());
        employeeService.findByUser(user);
        Project project = projectMapper.toModal(projectDTO);
        return ResponseEntity.ok(
                projectMapper.toDto(
                        projectService.addProject(project, projectDTO.getDepartment().getId())
                )
        );
    }

    @PostMapping("/get_all/open")
    public ResponseEntity<?> getAllOpenProjects(@RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findAllOpenProjectsWithPaginationAndSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(projectMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/by_employee/open/{id}")
    public ResponseEntity<?> getAllOpenProjectsByEmployee(@RequestBody DataTablePagingRequest pagingRequest,
                                                          @PathVariable Long id, Authentication authentication) {
        Employee employee;
        if (id != 0) employee = employeeService.find(id);
        else employee = employeeService.findByUser(
                userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication)));
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findAllOpenProjectsByEmployeeWithPaginationAndSearch(
                                employee,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(projectMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/by_department/open/{id}")
    public ResponseEntity<?> getAllOpenProjectsByDepartmentWithPaginationAndSearch(
            @RequestBody DataTablePagingRequest pagingRequest, @PathVariable Long id, Authentication authentication) {
        Department department;
        if (id != 0) department = departmentService.find(id);
        else
            department = employeeService.findByUser(
                    userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))).getDepartment();
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findAllOpenProjectsByDepartmentWithPaginationAndSearch(
                                department,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(projectMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/get_all/closed")
    public ResponseEntity<?> getAllClosedProjectsWithPaginationAndSearch(
            @RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        projectService.findAllClosedProjectsWithPaginationAndSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(projectMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activateProject(@PathVariable Long id) {
        if (!projectService.activate(projectService.find(id)))
            throw new OperationExecutionException(
                    messageSource.getMessage("project.error.activate", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/inactivate/{id}")
    public ResponseEntity<?> inactivateProject(@PathVariable Long id) {
        if (!projectService.inactivate(projectService.find(id)))
            throw new OperationExecutionException(
                    messageSource.getMessage("project.error.deactivate", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO) {
        Project project = projectService.find(id);
        project.setName(projectDTO.getName());
        project.setModifiedDate(new Date(System.currentTimeMillis()));
        project = projectService.save(project);
        if (!Objects.equals(project.getDepartment().getId(), projectDTO.getDepartment().getId())
                && projectDTO.getDepartment().getId() != null) {
            project = projectService.assignProjectToDepartmentId(project, projectDTO.getDepartment().getId());
            if (!Objects.equals(project.getDepartment().getId(), projectDTO.getDepartment().getId())
                    && projectDTO.getDepartment().getId() != null)
                throw new ExistingDataException(
                        messageSource.getMessage("project.error.transfer", null, LocaleContextHolder.getLocale()));
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/close/{id}")
    public ResponseEntity<?> closeProject(@PathVariable Long id) {
        Project project = projectService.find(id);
        if (project.isActive()) throw new ActiveStatusDataException(
                messageSource.getMessage("project.error.close.active", null, LocaleContextHolder.getLocale()));
        if (!projectService.closeProject(projectService.find(id)))
            throw new ExistingDataException(
                    messageSource.getMessage("project.error.close.task", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign/employee")
    public ResponseEntity<?> assignEmployeeToProject(@Valid @RequestBody EmployeePositionDTO employeePositionDTO) {
        if (projectService.assignToProject(
                employeeService.find(employeePositionDTO.getEmployee().getId()),
                projectService.find(employeePositionDTO.getProject().getId()),
                departmentService.findPosition(employeePositionDTO.getPosition().getId())).getId() == null)
            throw new OperationExecutionException(
                    messageSource.getMessage("project.error.assign.user", null, LocaleContextHolder.getLocale()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cancel/employee")
    public ResponseEntity<?> cancelEmployeeFromProject(@Valid @RequestBody EmployeePositionDTO employeePositionDTO) {
        EmployeePosition employeePosition = projectService.findEmployeePositions(
                        employeeService.find(employeePositionDTO.getEmployee().getId()))
                .stream().filter(obj ->
                        obj.getEmployee().getId().equals(employeePositionDTO.getEmployee().getId())
                                && obj.getProject().getId().equals(employeePositionDTO.getProject().getId())).findFirst().orElse(null);
        if (projectService.changeEmployeeStateInProject(employeePosition, false).isActive())
            throw new OperationExecutionException(
                    messageSource.getMessage("project.error.remove.user", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }
}
