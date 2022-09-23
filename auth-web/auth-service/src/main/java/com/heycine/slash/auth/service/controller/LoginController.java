package com.heycine.slash.auth.service.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * token 控制
 *
 * @author Alikes
 */
@Controller
public class LoginController {
    
    @GetMapping({ "/", "/index", "/home" })
    public String index(Model model) {

        return "index";
    }

    @GetMapping("/auth/login")
    public String login(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            return "redirect:/index";
        }

        return "login";
    }
    
}
