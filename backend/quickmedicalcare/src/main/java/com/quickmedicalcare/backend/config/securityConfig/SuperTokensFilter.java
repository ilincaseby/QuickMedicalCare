package com.quickmedicalcare.backend.config.securityConfig;

import com.quickmedicalcare.backend.RegisterLogin.SuperTokensInterface;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class SuperTokensFilter extends OncePerRequestFilter {

    private SuperTokensInterface superTokensAPI;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = request.getHeader("Authentication");
            String antiCsrfToken = request.getHeader("X-CSRF-TOKEN");
            if (token != null && antiCsrfToken != null) {
                token = token.replace("Bearer ", "");
                String userId = superTokensAPI.obtainAccess(token, antiCsrfToken);
                if (!userId.isEmpty()) {
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userId, null));
                    filterChain.doFilter(request, response);
                }
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
