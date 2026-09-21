package com.gms.gmsmvp.security;

import com.gms.gmsmvp.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GmsUserDetails implements UserDetails {

    private final Users users;
    private final List<String> permissions;

    public GmsUserDetails(
            Users users,
            List<String> permissions) {

        this.users = users;
        this.permissions = permissions;
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

        List<GrantedAuthority> authorities =
                new ArrayList<>();

        // Role authority
        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + getRoleName()
                )
        );

        // Permission authorities
        permissions.forEach(permission ->
                authorities.add(
                        new SimpleGrantedAuthority(
                                "PERMISSION_" + permission
                        )
                )
        );

        return authorities;
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

    public Users getUsers() {
        return users;
    }
}
