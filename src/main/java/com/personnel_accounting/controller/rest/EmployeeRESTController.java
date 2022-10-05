package com.personnel_accounting.controller.rest;

import com.personnel_accounting.entity.domain.Department;
import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.domain.Project;
import com.personnel_accounting.entity.dto.EmployeeDTO;
import com.personnel_accounting.entity.enums.Role;
import com.personnel_accounting.entity.pagination.DataTablePagingRequest;
import com.personnel_accounting.exception.ExistingDataException;
import com.personnel_accounting.exception.OperationExecutionException;
import com.personnel_accounting.mapper.EmployeeMapper;
import com.personnel_accounting.mapper.UserMapper;
import com.personnel_accounting.service.department.DepartmentService;
import com.personnel_accounting.service.employee.EmployeeService;
import com.personnel_accounting.service.project.ProjectService;
import com.personnel_accounting.service.user.UserService;
import com.personnel_accounting.utils.AuthenticationUtil;
import com.personnel_accounting.utils.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeRESTController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/registration")
    public ResponseEntity<?> registerEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeMapper.toModal(employeeDTO);
        employee.setUser(userMapper.toModal(employeeDTO.getUser()));
        employee.setCreateDate(new Date(System.currentTimeMillis()));
        if (!userService.registerUser(employee.getUser(),
                "{bcrypt}" + (new BCryptPasswordEncoder()).encode(employee.getUser().getPassword()),
                employeeDTO.getName(), Role.EMPLOYEE, employeeDTO.getProfile().getEmail()))
            throw new ExistingDataException(
                    messageSource.getMessage("user.error.existing", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/employee/get_all")
    public ResponseEntity<?> getEmployees(@RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findAllEmployeeWithPaginationAndSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/get_all/active")
    public ResponseEntity<?> getAllActiveEmployees(@RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findAllActiveEmployeeWithPaginationAndSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/get_all/dismissed")
    public ResponseEntity<?> getDismissedEmployees(@RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findAllDismissedEmployeeWithPaginationAndSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/get_all/admins")
    public ResponseEntity<?> getAllActiveAdmins(@RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findAllActiveAdminsWithPaginationAndSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/get_all/free")
    public ResponseEntity<?> getAllFreeEmployees(@RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findAllFreeAndActiveEmployeesWithPaginationAndSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/get_all/department/{id}")
    public ResponseEntity<?> getAllEmployeeByDepartment(@RequestBody DataTablePagingRequest pagingRequest,
                                                        @PathVariable Long id, Authentication authentication) {
        Department department;
        if (id != 0) department = departmentService.find(id);
        else
            department = employeeService.findByUser(
                    userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))).getDepartment();
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findEmployeesByDepartmentWithPaginationAndSearch(
                                department,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/get_all/by_project/{id}")
    public ResponseEntity<?> getAllEmployeeByProject(
            @RequestBody DataTablePagingRequest pagingRequest, @PathVariable Long id) {
        Project project = projectService.find(id);
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findEmployeesByProjectWithPaginationAndSearch(
                                project,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/get_with_project/department/{id}")
    public ResponseEntity<?> getEmployeesWithProjectByDepartment(@RequestBody DataTablePagingRequest pagingRequest,
                                                                 @PathVariable Long id, Authentication authentication) {
        Department department;
        if (id != 0) department = departmentService.find(id);
        else
            department = employeeService.findByUser(
                    userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))).getDepartment();
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findEmployeesWithOpenProjectByDepartmentWithPaginationAndSearch(
                                department,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/get_all/assigned")
    public ResponseEntity<?> getAllAssignedEmployees(@RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        employeeService.findAllAssignedAndActiveEmployeesWithPaginationAndSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(employeeMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/api/employee/assign/department")
    public ResponseEntity<?> assignEmployeeToDepartment(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeService.find(employeeDTO.getId());
        Department department = departmentService.find(employeeDTO.getDepartment().getId());
        employee.setDepartment(department);
        employee = departmentService.assignToDepartment(employee, department);
        if (!employee.getDepartment().getId().equals(employeeDTO.getDepartment().getId()))
            throw new OperationExecutionException(
                    messageSource.getMessage("user.error.assign", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/employee/remove/{id}")
    public ResponseEntity<?> removeUser(@PathVariable Long id) {
        if (!employeeService.removeWithInactivation(employeeService.find(id)))
            throw new OperationExecutionException(
                    messageSource.getMessage("user.error.remove", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/employee/edit")
    public ResponseEntity<?> editEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeService.find(employeeDTO.getId());
        employee = employeeService.editEmployeeName(employee, employeeDTO.getName());
        employee.setUser(userService.changeEmployeeRole(employee, employeeDTO.getUser().getAuthority().getRole()));
        userService.save(employee.getUser());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/employee/activate/{username}")
    public ResponseEntity<?> activateUser(@PathVariable String username) {
        Employee employee = employeeService.findByUser(userService.find(username));
        if (!userService.activate(employee.getUser()) || !employeeService.activate(employee))
            throw new OperationExecutionException(
                    messageSource.getMessage("user.error.activate", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/employee/inactivate/{username}")
    public ResponseEntity<?> inactivateUser(@PathVariable String username) {
        Employee employee = employeeService.findByUser(userService.find(username));
        if (!userService.inactivate(employee.getUser()) || !employeeService.inactivate(employee))
            throw new OperationExecutionException(
                    messageSource.getMessage("user.error.deactivate", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/employee/get_by_role/{role}")
    public ResponseEntity<?> getEmployeesByRole(@PathVariable Role role) {
        List<EmployeeDTO> employeeDTOList = userService.findByRole(role).stream()
                .map(user -> employeeMapper.toDto(employeeService.findByUser(user)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(employeeDTOList);
    }
}
