package com.personnel_accounting.controller.page;

import com.personnel_accounting.entity.domain.User;
import com.personnel_accounting.entity.enums.Role;
import com.personnel_accounting.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class PageController {
    private final String emailPassword = "EmailPassword123";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        return "authorization/authorization";
    }

    @GetMapping("/page")
    public String showLoginPageWhenError(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        return "redirect:/login";
    }

    @GetMapping("/page/**")
    public String getLoginInfo(HttpServletRequest request, Model model, Authentication authentication) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("main_page.title",
                null, LocaleContextHolder.getLocale()));
        String url = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
        if (url.endsWith("page")) {
            OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    authenticationToken.getAuthorizedClientRegistrationId(), authentication.getName());

            User user = getUserInfo(client);
            if (user != null && user.isActive())
                model.addAttribute("token", getJwtToken(user));
            else if (user != null && !user.isActive())
                return "redirect:/login?disabled";
        }
        return "page";
    }

    @GetMapping("/main")
    public String showPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("main_page.title",
                null, LocaleContextHolder.getLocale()));
        return "main-page/main-page";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("navbar.profile",
                null, LocaleContextHolder.getLocale()));
        return "profile-page";
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        return "authorization/registration";
    }

    @GetMapping("/telephone-directory")
    public String getTelephoneDirectoryPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("sidebar.telephone_directory",
                null, LocaleContextHolder.getLocale()));
        return "telephone-directory";
    }

    @GetMapping("/user")
    public String getUserPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("user.title",
                null, LocaleContextHolder.getLocale()));
        return "user";
    }

    @GetMapping("/department")
    public String getDepartmentPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("department.title",
                null, LocaleContextHolder.getLocale()));
        return "department";
    }

    @GetMapping("/position")
    public String getPositionPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("position.title",
                null, LocaleContextHolder.getLocale()));
        return "position";
    }

    @GetMapping("/project")
    public String getProjectPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("project.title",
                null, LocaleContextHolder.getLocale()));
        return "project";
    }

    @GetMapping("/task")
    public String getTaskPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("task.title",
                null, LocaleContextHolder.getLocale()));
        return "task";
    }

    @GetMapping("/my-task")
    public String getMyTaskPage(Model model) {
        model.addAttribute("lang", LocaleContextHolder.getLocale().getLanguage());
        model.addAttribute("title", messageSource.getMessage("my_task.title",
                null, LocaleContextHolder.getLocale()));
        return "my-task";
    }

    private String getJwtToken(User user) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://localhost:8080/authenticate",
                        User.builder().username(user.getUsername()).password(emailPassword).isActive(user.isActive()).build(),
                        String.class);
        return response.getBody();
    }

    private User getUserInfo(OAuth2AuthorizedClient client) {
        String userInfoEndpointUri = getUserInfoEndpointUri(client);

        if (!userInfoEndpointUri.isEmpty()) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();

            String userEmail = (String) userAttributes.get("email"), userName = (String) userAttributes.get("name");

            User user = userService.find((String) userAttributes.get("email"));
            if (user == null)
                userService.registerUser(User.builder().username(userEmail).isActive(false).build(),
                        "{bcrypt}" + passwordEncoder.encode(emailPassword),
                        userName, Role.EMPLOYEE, userEmail);
            return userService.find(userEmail);
        } else return null;
    }

    private String getUserInfoEndpointUri(OAuth2AuthorizedClient client) {
        return client.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();
    }
}
