package com.personnel_accounting.service.department;

import com.personnel_accounting.entity.domain.Department;
import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.domain.Position;
import com.personnel_accounting.entity.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface DepartmentService {

    Department addDepartment(Department department);
    Department editDepartmentName(Department department, String name);
    boolean closeDepartment(Department department);
    Department changeDepartmentState(Department department, boolean isActive);

    Department save(Department department);
    boolean remove(Department department);
    boolean inactivate(Department department);
    boolean activate(Department department);
    Department find(Long id);

    Page<Department> findAllOpenDepartmentsWithPaginationAnsSearch(Pageable pageable, String search);
    Page<Department> findAllClosedDepartmentsWithPaginationAnsSearch(Pageable pageable, String search);

    List<Project> findProjects(Department department);
    Page<Project> findOpenProjectsByDepartmentWithPaginationAndSearch(Department department, Pageable pageable, String search);

    List<Employee> findEmployees(Department department);
    Employee assignToDepartment(Employee employee, Department department);

    Position addPosition(Position position);
    Position editPosition(Position position);
    boolean removePositionById(Long id);
    Position findPosition(Long id);

    List<Position> findAllPositions();
    List<Position> findAllPositions(Sort sort);
    Page<Position> findAllPositionsWithPaginationAnsSearch(Pageable pageable, String search);
}
