package com.personnel_accounting.entity.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User implements UserDetails {

    @Id
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", unique = true, nullable = false, length = 100)
    private String password;

    @Column(name = "enabled", columnDefinition = "boolean default false")
    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Authority> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}
