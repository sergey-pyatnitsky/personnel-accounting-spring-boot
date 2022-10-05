package com.personnel_accounting.service.user;

import com.personnel_accounting.entity.domain.Authority;
import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.domain.User;
import com.personnel_accounting.entity.enums.Role;

import java.util.List;
import java.util.Set;

public interface UserService {

    Set<Authority> getAuthoritiesByUsername(String username);

    User changeAuthData(User user, String password);
    Role getMaxRole(User user);
    Set<Authority> setAllRoles(Role role);
    Role getMaxRole(Set<Authority> roles);
    User addUserRole(User user, Role role);
    User removeUserRole(User user, Role role);
    User changeEmployeeRole(Employee employee, Role role);
    boolean registerUser(User user, String pass, String name, Role role, String email);

    User find(String username);
    List<User> findByRole(Role role);

    boolean inactivate(User user);
    boolean activate(User user);

    User save(User user);
    User save(User user, Role role);
    boolean remove(User user);
}
