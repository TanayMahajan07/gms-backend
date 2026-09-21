package com.gms.gmsmvp.util;

import com.gms.gmsmvp.entity.Users;
import com.gms.gmsmvp.security.GmsUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * Returns the currently authenticated GMS user.
     */
    public static Users getUserDetail() {

        SecurityContext securityContext =
                SecurityContextHolder.getContext();

        if (securityContext == null) {
            return null;
        }

        Authentication authentication =
                securityContext.getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof GmsUserDetails userDetails) {
            return userDetails.getUsers();
        }

        return null;
    }

    /**
     * Returns the currently logged-in user ID.
     */
    public static Long getLoggedInUserId() {

        Users user = getUserDetail();

        return user != null
                ? user.getId()
                : null;
    }

    public static Long getLoggedInGymId() {

        Users user = getUserDetail();

        return user != null
                ? user.getGymId()
                : null;
    }

    /**
     * Checks whether the current user has the given role.
     */
    public static boolean hasRole(String role) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_" + role));
    }

    /**
     * Checks whether the current user has the given permission.
     */
    public static boolean hasPermission(String permission) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("PERMISSION_" + permission));
    }

}
