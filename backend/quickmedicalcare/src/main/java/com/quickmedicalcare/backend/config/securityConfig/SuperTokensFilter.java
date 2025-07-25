package com.quickmedicalcare.backend.config.securityConfig;

import com.quickmedicalcare.backend.registerLogin.SuperTokensInterface;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@AllArgsConstructor
public class SuperTokensFilter extends OncePerRequestFilter {

    private SuperTokensInterface superTokensAPI;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = request.getHeader("Authorization");
            String antiCsrfToken = request.getHeader("X-CSRF-TOKEN");
            if (token != null && antiCsrfToken != null) {
                token = token.replace("Bearer ", "");
                System.out.println(token);
                String userId = superTokensAPI.obtainAccess(token, antiCsrfToken);
                System.out.println(userId);
                if (!userId.isEmpty()) {
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>()));
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        filterChain.doFilter(request, response);
    }
}
