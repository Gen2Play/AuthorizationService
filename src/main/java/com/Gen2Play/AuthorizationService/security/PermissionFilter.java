package com.Gen2Play.AuthorizationService.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PermissionFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // ✅ Bỏ qua Swagger UI & API Docs chính xác
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/swagger-resources")
            || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        System.out.println("🔍 [PermissionFilter] Checking request: " + request.getRequestURI());

        String permissionsHeader = request.getHeader("X-Permissions");
        if (permissionsHeader == null || permissionsHeader.isEmpty()) {
            System.out.println("❌ [PermissionFilter] Missing required permissions.");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing required permissions.");
            return;
        }

        List<String> permissions = Arrays.asList(permissionsHeader.split(","));
        System.out.println("✅ [PermissionFilter] Permissions: " + permissions);

        // Chuyển đổi quyền thành SimpleGrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Tạo Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
