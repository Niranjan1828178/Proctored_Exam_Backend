package com.proctoredExam.exam.security;

import com.proctoredExam.exam.entity.ApprovalStatus;
import com.proctoredExam.exam.entity.Role;
import com.proctoredExam.exam.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ClientApprovalFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip auth endpoints
        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();
        String method = request.getMethod();

        // Check if the endpoint is a client-exclusive modification endpoint
        boolean isClientEndpoint = false;
        if (path.startsWith("/api/tests") && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE") || path.contains("my-tests"))) {
            isClientEndpoint = true;
        }
        if (path.startsWith("/api/questions") && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))) {
            isClientEndpoint = true;
        }

        if (isClientEndpoint) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                
                if (user.getRole() == Role.CLIENT) {
                    if (user.getClientProfile() == null || user.getClientProfile().getStatus() != ApprovalStatus.APPROVED) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Your account is pending admin approval. You cannot perform this action.\"}");
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
