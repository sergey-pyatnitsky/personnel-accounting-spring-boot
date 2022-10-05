package com.personnel_accounting.service.user;

import com.personnel_accounting.entity.domain.Authority;
import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.domain.Profile;
import com.personnel_accounting.entity.domain.User;
import com.personnel_accounting.entity.enums.Role;
import com.personnel_accounting.repository.AuthorityRepository;
import com.personnel_accounting.repository.EmployeeRepository;
import com.personnel_accounting.repository.UserRepository;
import com.personnel_accounting.service.email.EmailService;
import com.personnel_accounting.utils.ValidationUtil;
import com.personnel_accounting.validation.RegexpValidator;
import com.personnel_accounting.validation.UserValidator;
import com.personnel_accounting.validation.regexp.RegexpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private RegexpValidator regexpValidator;

    @Autowired
    private MessageSource messageSource;

    @Override
    public Set<Authority> getAuthoritiesByUsername(String username) {
        User user = userRepository.findById(username).orElse(null);
        if (user != null && user.getRoles() != null)
            return user.getRoles();
        return null;
    }

    @Override
    public User changeAuthData(User user, String password) {
        user.setPassword(password);
        ValidationUtil.validate(user, userValidator);
        user.setPassword("{bcrypt}" + (new BCryptPasswordEncoder()).encode(password));
        return userRepository.save(user);
    }

    @Override
    public Role getMaxRole(User user) {
        user = userRepository.findById(user.getUsername()).orElse(null);
        if (user == null) return null;

        for (int roleLoop = 0; roleLoop < Role.values().length; ++roleLoop) {
            if (user.getRoles().contains(authorityRepository.findByName(Role.values()[roleLoop].name()).orElse(null)))
                return Role.values()[roleLoop];
        }

        return null;
    }

    @Override
    public Set<Authority> setAllRoles(Role role) {
        Set<Authority> authorities = new HashSet<>();
        for (int roleLoop = Role.values().length - 1; roleLoop >= 0; --roleLoop) {
            int finalRoleLoop = roleLoop;
            authorities.add(authorityRepository.findByName(role.name())
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(Role.values()[finalRoleLoop].name()).build())));
            if (Role.values()[roleLoop].equals(role)) break;
        }
        return authorities;
    }

    @Override
    public Role getMaxRole(Set<Authority> roles) {
        for (int roleLoop = 0; roleLoop < Role.values().length; ++roleLoop) {
            if (roles.contains(authorityRepository.findByName(Role.values()[roleLoop].name()).orElse(null)))
                return Role.values()[roleLoop];
        }
        return null;
    }

    @Override
    public User addUserRole(User user, Role role) {
        user = userRepository.findById(user.getUsername()).orElse(null);
        if (user != null) {
            user.getRoles().add(authorityRepository.findByName(role.name())
                    .orElse(authorityRepository.save(Authority.builder().name(role.name()).build())));
        }
        return null;
    }

    @Override
    public User removeUserRole(User user, Role role) {
        user = userRepository.findById(user.getUsername()).orElse(null);
        if (user != null)
            user.getRoles().removeIf(authority -> authority.getName().equals(role.name()));
        return null;
    }

    @Override
    public User changeEmployeeRole(Employee employee, Role role) {
        employee = employeeRepository.findById(employee.getId()).orElse(null);
        employee.getUser().setRoles(null);
        if (employee.getUser().getRoles() == null) employee.getUser().setRoles(new HashSet<>());
        for (int roleLoop = Role.values().length - 1; roleLoop >= 0; --roleLoop) {
            int finalRoleLoop = roleLoop;
            employee.getUser().getRoles().add(authorityRepository.findByName(role.name())
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(Role.values()[finalRoleLoop].name()).build())));
            if (Role.values()[roleLoop].equals(role)) break;
        }
        return userRepository.save(employee.getUser());
    }

    @Override
    public boolean registerUser(User user, String pass, String name, Role role, String email) {
        ValidationUtil.validate(user, userValidator);
        ValidationUtil.validate(RegexpEntity.RegexpValidationBuilder.anApiError().withEmail(email).build(), regexpValidator);
        user.setPassword(pass);
        User tempUser = userRepository.findById(user.getUsername()).orElse(null);
        if (tempUser == null) {
            user = saveAllRoles(user, role);
            if (!role.equals(Role.SUPER_ADMIN)) {
                Employee employee = Employee.builder().name(name).isActive(false).user(user)
                        .profile(new Profile()).createDate(new Date(System.currentTimeMillis())).build();
                employee.getProfile().setEmail(email);
                employee.getProfile().setImageId("1fvYTCz3IanxXOeRZgy1J98B0LN6chjOA");
                employee.getProfile().setCreateDate(new Date(System.currentTimeMillis()));
                employeeRepository.save(employee);
                emailService.sendSimpleEmail(email,
                        messageSource.getMessage("email.welcome.title", null, LocaleContextHolder.getLocale()),
                        messageSource.getMessage("email.welcome.message", null, LocaleContextHolder.getLocale()));
            }
            return true;
        } else return false;
    }

    @Override
    public User find(String username) {
        return userRepository.findById(username).orElse(null);
    }

    @Override
    public List<User> findByRole(Role role) {
        Authority authority = authorityRepository.findByName(role.name()).orElse(null);
        if (authority == null) return null;

        List<User> users = userRepository.findUsersByRolesContains(authority);
        if (users == null) return null;

        for (Role roleLoop : Role.values()) {
            if (roleLoop.equals(role)) break;
            users = users.stream()
                    .filter(user -> !user.getRoles().contains(authorityRepository.findByName(roleLoop.name()).orElse(null)))
                    .collect(Collectors.toList());
        }
        return users;
    }

    @Override
    public boolean inactivate(User user) {
        int isActive = userRepository.changeActiveStatus(user.getUsername(), false);
        if (isActive == 1) {
            employeeRepository.findByUser(user).getProfile().getEmail();
            emailService.sendSimpleEmail(employeeRepository.findByUser(user).getProfile().getEmail(),
                    messageSource.getMessage("email.deactivate.success.title", null, LocaleContextHolder.getLocale()),
                    messageSource.getMessage("email.deactivate.success.message", null, LocaleContextHolder.getLocale()));
        }
        return isActive == 1;
    }

    @Override
    public boolean activate(User user) {
        int isActive = userRepository.changeActiveStatus(user.getUsername(), true);
        if (isActive == 1) {
            employeeRepository.findByUser(user).getProfile().getEmail();
            emailService.sendSimpleEmail(employeeRepository.findByUser(user).getProfile().getEmail(),
                    messageSource.getMessage("email.activate.success.title", null, LocaleContextHolder.getLocale()),
                    messageSource.getMessage("email.activate.success.message", null, LocaleContextHolder.getLocale()));
        }
        return isActive == 1;
    }

    @Override
    public User save(User user) {
        ValidationUtil.validate(user, userValidator);
        return userRepository.save(user);
    }

    @Override
    public User save(User user, Role role) {
        ValidationUtil.validate(user, userValidator);
        user = userRepository.findById(user.getUsername()).orElse(user);

        Authority authority;
        if (authorityRepository.findByName(role.name()).isPresent())
            authority = authorityRepository.findByName(role.name()).orElse(null);
        else authority = Authority.builder().name(role.name()).build();

        if (user.getRoles() == null) {
            Set<Authority> authorities = new HashSet<>();
            authorities.add(authority);
            user.setRoles(authorities);
        } else
            user.getRoles().add(authority);
        user = userRepository.save(user);
        saveAllRoles(user, role);
        return user;
    }

    private User saveAllRoles(User user, Role role) {
        if (user.getRoles() == null) user.setRoles(new HashSet<>());
        for (int roleLoop = Role.values().length - 1; roleLoop >= 0; --roleLoop) {
            int finalRoleLoop = roleLoop;
            user.getRoles().add(authorityRepository.findByName(role.name())
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(Role.values()[finalRoleLoop].name()).build())));
            if (Role.values()[roleLoop].equals(role)) break;
        }
        return userRepository.save(user);
    }

    @Override
    public boolean remove(User user) {
        if (authorityRepository.removeByName(user.getUsername())) {
            return userRepository.removeByUsername(user.getUsername());
        }
        return false;
    }
}

