package com.gms.gmsmvp.security;

import com.gms.gmsmvp.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class GmsUserDetails implements UserDetails {

    private final Users users;

    public GmsUserDetails(Users users) {
        this.users = users;
    }

    public Long getId() {
        return users.getId();
    }

    public String getFullName() {
        String lastName = users.getLastName() == null ? "" : " " + users.getLastName();
        return users.getFirstName() + lastName;
    }

    public String getRoleName() {
        return users.getRole().getRoleName();
    }

    public Long getGymId() {
        return users.getGymId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRoleName()));
    }

    @Override
    public String getPassword() {
        return users.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return users.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return users.getActive() == null || users.getActive();
    }
}
