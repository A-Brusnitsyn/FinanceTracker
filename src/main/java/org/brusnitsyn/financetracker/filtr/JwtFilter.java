package org.brusnitsyn.financetracker.filtr;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.brusnitsyn.financetracker.service.CustomUserDetailService;
import org.brusnitsyn.financetracker.service.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailService userDetailService;

    public JwtFilter(JwtService jwtService, CustomUserDetailService customUserDetailService) {
        this.jwtService = jwtService;
        this.userDetailService = customUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String email = jwtService.extractEmail(token);

        UserDetails userDetails = userDetailService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
