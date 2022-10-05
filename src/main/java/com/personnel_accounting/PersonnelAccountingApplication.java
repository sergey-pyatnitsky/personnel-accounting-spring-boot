package com.personnel_accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.personnel_accounting.repository")
public class PersonnelAccountingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonnelAccountingApplication.class, args);
    }

}
