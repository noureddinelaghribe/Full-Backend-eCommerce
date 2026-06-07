package com.example.full.auth.and.jwt.web;

import com.example.full.auth.and.jwt.model.Role;
import com.example.full.auth.and.jwt.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserControllerAdvice {

    @ModelAttribute("currentUser")
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            return null;
        }
        if (auth.getPrincipal() instanceof User user) {
            return user;
        }
        return null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        User u = currentUser();
        return u != null && Role.ROLE_ADMIN.equals(u.getRole());
    }

    @ModelAttribute("isSeller")
    public boolean isSeller() {
        User u = currentUser();
        return u != null && (Role.ROLE_SELLER.equals(u.getRole()) || Role.ROLE_ADMIN.equals(u.getRole()));
    }
}
