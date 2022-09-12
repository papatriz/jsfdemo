package com.papatriz.jsfdemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final RoleHandler roleHandler;
    @Autowired
    public LoginSuccessHandler(RoleHandler roleHandler) {
        this.roleHandler = roleHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // String redirectURL = request.getContextPath();
        String redirectURL = request.getContextPath() +"/"+ roleHandler.getLandingPageForRole(userDetails.getRole());
        System.out.println("REDIRECT TO "+redirectURL);
        response.sendRedirect(redirectURL);
    }
}