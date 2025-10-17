package com.hakaton.hakaton.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        String redirectUrl = null;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_ADMINISTRADOR")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (authorityName.equals("ROLE_JURADO")) {
                redirectUrl = "/jurado/dashboard_jurado";
                break;
            } else if (authorityName.equals("ROLE_PARTICIPANTE")) {
                redirectUrl = "/participante/dashboard_participantes";
                break;
            }
        }

        if (redirectUrl == null) {
            throw new IllegalStateException("No se pudo determinar la URL de redirección para el rol del usuario.");
        }

        response.sendRedirect(redirectUrl);
    }
}
