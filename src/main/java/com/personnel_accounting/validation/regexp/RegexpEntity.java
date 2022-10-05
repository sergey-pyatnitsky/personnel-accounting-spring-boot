package com.personnel_accounting.validation.regexp;

import lombok.Data;

@Data
public class RegexpEntity {
    private String password;
    private String username;
    private String email;
    private String phone;

    public RegexpEntity(String password, String username, String email, String phone) {
        this.password = password;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public static final class RegexpValidationBuilder {
        private String password;
        private String username;
        private String email;
        private String phone;

        public RegexpValidationBuilder() {
        }

        public static RegexpEntity.RegexpValidationBuilder anApiError() {
            return new RegexpEntity.RegexpValidationBuilder();
        }

        public RegexpEntity.RegexpValidationBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public RegexpEntity.RegexpValidationBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public RegexpEntity.RegexpValidationBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public RegexpEntity.RegexpValidationBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public RegexpEntity build() {
            return new RegexpEntity(password, username, email, phone);
        }
    }
}
