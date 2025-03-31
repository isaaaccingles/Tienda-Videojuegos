package com.tiendavideojuegos.tienda.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.security.Key;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "mi_clave_secreta_muy_segura_y_larga";



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("Token: " + token);

            try {
                Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
                @SuppressWarnings("deprecation")
                Claims claims = Jwts.parser()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();
                List<?> rawRoles = claims.get("roles", List.class);
                List<String> roles = rawRoles != null ?
                        rawRoles.stream()
                                .filter(String.class::isInstance)
                                .map(String.class::cast)
                                .collect(Collectors.toList()) :
                        Collections.emptyList();
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                System.out.println("Username from token: " + username);
                System.out.println("Roles from token: " + authorities);

                if (username != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    System.out.println("Created auth object with roles: " + auth.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("Authentication set for: " + username + " with roles: " + auth.getAuthorities());
                    System.out.println("SecurityContext roles after set: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                }
            } catch (Exception e) {
                System.out.println("JWT Validation Failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT inválido: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("No valid Bearer token found");
        }

        filterChain.doFilter(request, response);
    }
}