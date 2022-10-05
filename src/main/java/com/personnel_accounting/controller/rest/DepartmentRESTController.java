package com.personnel_accounting.controller.rest;

import com.personnel_accounting.entity.domain.Department;
import com.personnel_accounting.entity.dto.DepartmentDTO;
import com.personnel_accounting.entity.pagination.DataTablePagingRequest;
import com.personnel_accounting.exception.ActiveStatusDataException;
import com.personnel_accounting.exception.ExistingDataException;
import com.personnel_accounting.exception.OperationExecutionException;
import com.personnel_accounting.mapper.DepartmentMapper;
import com.personnel_accounting.mapper.ProjectMapper;
import com.personnel_accounting.service.department.DepartmentService;
import com.personnel_accounting.utils.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;

@RestController
@RequestMapping("api/department")
public class DepartmentRESTController {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/add")
    public ResponseEntity<?> addDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        Department department = departmentMapper.toModal(departmentDTO);
        department.setActive(false);
        department.setCreateDate(new Date(System.currentTimeMillis()));

        department = departmentService.addDepartment(department);
        if (department.getId() == null)
            throw new ExistingDataException(
                    messageSource.getMessage("department.error.add", null, LocaleContextHolder.getLocale()));
        return new ResponseEntity<>(departmentMapper.toDto(department), HttpStatus.OK);
    }

    @PostMapping("/get_all/open")
    public ResponseEntity<?> getAllOpenDepartmentsWithPaginationAnsSearch(
            @RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        departmentService.findAllOpenDepartmentsWithPaginationAnsSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(departmentMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PostMapping("/get_all/closed")
    public ResponseEntity<?> getAllClosedDepartmentsWithPaginationAnsSearch(
            @RequestBody DataTablePagingRequest pagingRequest) {
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        departmentService.findAllClosedDepartmentsWithPaginationAnsSearch(
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(departmentMapper::toDto), pagingRequest.getDraw()
                )
        );
    }


    @PostMapping("/projects/open/{id}")
    public ResponseEntity<?> getAllOpenProjectsByDepartmentWithPaginationAndSearch(
            @RequestBody DataTablePagingRequest pagingRequest, @PathVariable Long id) {
        Department department = departmentService.find(id);
        return ResponseEntity.ok(
                PaginationUtil.getPageFromResponse(
                        departmentService.findOpenProjectsByDepartmentWithPaginationAndSearch(
                                department,
                                PaginationUtil.convertRequestToPageable(pagingRequest),
                                pagingRequest.getSearch().getValue()
                        ).map(projectMapper::toDto), pagingRequest.getDraw()
                )
        );
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activateDepartmentById(@PathVariable Long id) {
        Department department = departmentService.find(id);
        if (!departmentService.activate(department))
            throw new OperationExecutionException(
                    messageSource.getMessage("department.error.activation", null, LocaleContextHolder.getLocale()) + " " + id);
        return new ResponseEntity<>(departmentMapper.toDto(department), HttpStatus.OK);
    }

    @PutMapping("/inactivate/{id}")
    public ResponseEntity<?> inactivateDepartmentById(@PathVariable Long id) {
        Department department = departmentService.find(id);
        if (!departmentService.inactivate(department))
            throw new OperationExecutionException(
                    messageSource.getMessage("department.error.deactivation", null, LocaleContextHolder.getLocale()) + " " + id);
        return new ResponseEntity<>(departmentMapper.toDto(department), HttpStatus.OK);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editDepartment(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        Department department = departmentService.find(id);
        departmentService.editDepartmentName(department, departmentDTO.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/close/{id}")
    public ResponseEntity<?> closeDepartmentById(@PathVariable Long id) {
        Department department = departmentService.find(id);
        if (department.isActive())
            throw new ActiveStatusDataException(
                    messageSource.getMessage("department.error.active.status", null, LocaleContextHolder.getLocale()));
        if (!departmentService.closeDepartment(departmentService.find(id)))
            throw new ActiveStatusDataException(
                    messageSource.getMessage("department.error.close", null, LocaleContextHolder.getLocale()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
