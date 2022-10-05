package com.personnel_accounting.service.department;

import com.personnel_accounting.entity.domain.*;
import com.personnel_accounting.entity.enums.TaskStatus;
import com.personnel_accounting.exception.ExistingDataException;
import com.personnel_accounting.repository.*;
import com.personnel_accounting.utils.ValidationUtil;
import com.personnel_accounting.validation.DepartmentValidator;
import com.personnel_accounting.validation.PositionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeePositionRepository employeePositionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DepartmentValidator departmentValidator;

    @Autowired
    private PositionValidator positionValidator;

    @Autowired
    private MessageSource messageSource;

    @Override
    public Department addDepartment(Department department) {
        ValidationUtil.validate(department, departmentValidator);
        return departmentRepository.findDepartmentByName(department.getName())
                .stream().allMatch(obj -> obj.getEndDate() != null)
                ? departmentRepository.save(department)
                : department;
    }

    @Override
    public boolean closeDepartment(Department department) {
        Department tempDepartment = departmentRepository.getReferenceById(department.getId());
        if (tempDepartment.getStartDate() == null) {
            return departmentRepository.removeById(tempDepartment.getId()) == 1;
        } else {
            List<Project> projects = projectRepository.findByDepartment(tempDepartment);
            if (projects.size() == 0 || projects.stream().noneMatch(obj -> obj.getEndDate() == null)) {
                tempDepartment.setEndDate(new Date(System.currentTimeMillis()));
                tempDepartment.setModifiedDate(new Date(System.currentTimeMillis()));
                departmentRepository.save(tempDepartment);
                return true;
            }
            return false;
        }
    }

    @Override
    public List<Project> findProjects(Department department) {
        return projectRepository.findByDepartment(department);
    }

    @Override
    public Page<Project> findOpenProjectsByDepartmentWithPaginationAndSearch(Department department, Pageable pageable, String search) {
        return projectRepository.findProjectsByDepartmentAndEndDateIsNull(department, pageable, search);
    }

    @Override
    public List<Employee> findEmployees(Department department) {
        return employeeRepository.findByDepartment(department);
    }

    @Override
    public Employee assignToDepartment(Employee employee, Department department) {
        if (employee.getDepartment() == null) {
            employee.setDepartment(department);
            return employeeRepository.save(employee);
        } else if (!employee.getDepartment().getId().equals(department.getId())) {
            List<EmployeePosition> employeePositions = employeePositionRepository.findByEmployee(employee);
            employeePositions.forEach(obj -> obj.setActive(false));
            employeePositionRepository.saveAll(employeePositions);

            employee.setDepartment(department);
            return employeeRepository.save(employee);
        }
        return employeeRepository.findById(employee.getId()).orElse(null);
    }

    @Override
    public Department changeDepartmentState(Department department, boolean isActive) {
        List<Project> projects = projectRepository.findByDepartment(department);
        projects.forEach(obj -> obj.setActive(isActive));
        projectRepository.saveAll(projects);

        List<Employee> employees = employeeRepository.findByDepartment(department);
        employees.forEach(obj -> obj.setActive(isActive));
        employeeRepository.saveAll(employees);

        department.setActive(false);
        return departmentRepository.save(department);
    }

    @Override
    public Department editDepartmentName(Department department, String name) {
        department.setName(name);
        department.setModifiedDate(new Date(System.currentTimeMillis()));
        ValidationUtil.validate(department, departmentValidator);
        return departmentRepository.save(department);
    }

    @Override
    public Position addPosition(Position position) {
        ValidationUtil.validate(position, positionValidator);
        position.setCreateDate(new Date(System.currentTimeMillis()));
        return positionRepository.findAll().stream().filter(obj -> obj.getName().equals(position.getName()))
                .findFirst().orElse(null) != null
                ? position
                : positionRepository.save(position);
    }

    @Override
    public Position editPosition(Position position) {
        ValidationUtil.validate(position, positionValidator);
        if (positionRepository.findByName(position.getName()) != null)
            throw new ExistingDataException(
                    messageSource.getMessage("position.alert.edit", null, LocaleContextHolder.getLocale()));

        Position positionFromDb = positionRepository.findById(position.getId()).orElse(null);
        if (positionFromDb != null) {
            positionFromDb.setName(position.getName());
            positionFromDb.setModifiedDate(new Date(System.currentTimeMillis()));
            return positionRepository.save(positionFromDb);
        }
        return null;
    }

    @Override
    public boolean removePositionById(Long id) {
        if (positionRepository.findById(id).isPresent()
                && !employeePositionRepository.findByPosition(positionRepository.findById(id).get()).isEmpty())
            throw new ExistingDataException(
                    messageSource.getMessage("position.alert.remove", null, LocaleContextHolder.getLocale()));
        return positionRepository.removeById(id) == 1;
    }

    @Override
    public List<Position> findAllPositions(Sort sort) {
        return positionRepository.findAll(sort);
    }

    @Override
    public Page<Position> findAllPositionsWithPaginationAnsSearch(Pageable pageable, String search) {
        return positionRepository.findAll(pageable, search);
    }

    @Override
    public Position findPosition(Long id) {
        return positionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Position> findAllPositions() {
        return positionRepository.findAll();
    }

    @Override
    public Department find(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Department> findAllOpenDepartmentsWithPaginationAnsSearch(Pageable pageable, String search) {
        return departmentRepository.findDepartmentsByEndDateIsNull(pageable, search);
    }

    @Override
    public Page<Department> findAllClosedDepartmentsWithPaginationAnsSearch(Pageable pageable, String search) {
        return departmentRepository.findDepartmentsByEndDateIsNotNull(pageable, search);
    }

    @Override
    public Department save(Department department) {
        ValidationUtil.validate(department, departmentValidator);
        return departmentRepository.save(department);
    }

    @Override
    public boolean remove(Department department) {
        return departmentRepository.removeById(department.getId()) == 1;
    }

    @Override
    public boolean inactivate(Department department) {
        Date date = new Date(System.currentTimeMillis());
        projectRepository.findByDepartment(department).forEach(project -> {
            taskRepository.findByProject(project).forEach(task -> task.setTaskStatus(TaskStatus.CLOSED));
            employeePositionRepository.findByProject(project)
                    .forEach(employeePosition ->
                            employeePositionRepository.changeActiveStatus(employeePosition.getId(), false, date));
            projectRepository.changeActiveStatus(project.getId(), false, date);
        });
        return departmentRepository.changeActiveStatus(department.getId(), false, date) == 1;
    }

    @Override
    public boolean activate(Department department) {
        Date date = new Date(System.currentTimeMillis());
        projectRepository.findByDepartment(department).forEach(project -> {
            employeePositionRepository.findByProject(project)
                    .forEach(employeePosition ->
                            employeePositionRepository.changeActiveStatus(employeePosition.getId(), true, date));
            projectRepository.changeActiveStatus(project.getId(), true, date);
        });
        department.setStartDate(date);
        department = departmentRepository.save(department);
        return departmentRepository.changeActiveStatus(department.getId(), true, date) == 1;
    }
}
